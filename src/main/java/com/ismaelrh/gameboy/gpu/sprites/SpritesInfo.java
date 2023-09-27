package com.ismaelrh.gameboy.gpu.sprites;

import com.ismaelrh.gameboy.cpu.memory.MemoryInterceptor;

public class SpritesInfo extends MemoryInterceptor {

    private Sprite[] sprites = new Sprite[40];

    public SpritesInfo() {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = new Sprite(i);
        }
    }

    @Override
    public byte onWrite(char address, byte data) {
        if (address >= 0xFE00 && address <= 0xFE9F) {
            //Must now which sprite number I'm writing to (4 bytes per sprite)
            int spriteNumber = (address - 0xFE00) / 4;
            int bytePosition = (address - 0xFE00) - spriteNumber * 4;
            char spriteStartAddress = (char) ((0xFE00 + 4 * spriteNumber) & 0xFFFF);

            byte[] spriteData = memory.readRange(spriteStartAddress, 4);
            spriteData[bytePosition] = data;
            refreshSprite(spriteNumber, spriteData);
        }
        return super.onWrite(address, data);
    }

    //DOC: https://gbdev.gg8.se/wiki/articles/Video_Display
    //Can return nulls
    public Sprite[] getSpritesToDrawOnLine(int screenLY, int spriteSizeMode) {
        Sprite[] result = new Sprite[10];
        int added = 0;
        for (int i = 0; i < sprites.length; i++) {
            if (spriteIsConsideredForLine(screenLY, i, spriteSizeMode)) {
                result[added] = sprites[i];
                added++;
            }
            if (added >= 10) {
                return result;
            }
        }
        Sprite[] notNullResult = new Sprite[added];
        System.arraycopy(result, 0, notNullResult, 0, added);
        return notNullResult;
    }

    private boolean spriteIsConsideredForLine(int screenLY, int spritePos, int spriteSizeMode) {
        if(screenLY==40 && spritePos==4){
            int a = 3;
        }
        int spriteHeightPixels = spriteSizeMode == 0 ? 8 : 16;
        Sprite sprite = sprites[spritePos];
        return
                screenLY >= sprite.getPosY() &&
                        screenLY < sprite.getPosY() + spriteHeightPixels;

    }

    //Refresh internal structure of data, and also the line.
    //Do not create a new sprite!
    public void refreshSprite(int spriteNumber, byte[] data) {
        Sprite currentSprite = sprites[spriteNumber];
        currentSprite.refresh(data);
    }


}
