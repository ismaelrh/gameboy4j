package com.ismaelrh.gameboy.gpu.render;

import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.gpu.GpuRegisters;
import com.ismaelrh.gameboy.gpu.tiles.TileUtils;
import com.ismaelrh.gameboy.gpu.sprites.Sprite;
import com.ismaelrh.gameboy.gpu.sprites.SpriteRenderInfo;
import com.ismaelrh.gameboy.gpu.sprites.SpritesInfo;

import java.util.Arrays;

public class SpriteRenderer {

    private final GpuRegisters gpuRegisters;
    private final Memory memory;
    private final SpritesInfo spritesInfo;

    public SpriteRenderer(Memory memory, GpuRegisters gpuRegisters, SpritesInfo spritesInfo) {
        this.gpuRegisters = gpuRegisters;
        this.memory = memory;
        this.spritesInfo = spritesInfo;
    }

    public SpriteRenderInfo[] getSpriteRenderInfo(int line) {

        SpriteRenderInfo[] spritesRenderInfo = new SpriteRenderInfo[160];
        int[] spritesIndexes = new int[160];
        byte[] bgPriority = new byte[160];
        byte[] spritePalletIdxs = new byte[160];


        Sprite[] spritesToDraw = spritesInfo.getSpritesToDrawOnLine(line, gpuRegisters.spritesSizeMode);

        //We first print the lowest priority ones so they are then overriden.
        //so, higher posX has lower priority. higher sprite number has lower priority.
        Arrays.sort(spritesToDraw, (o1, o2) -> {
            if (o1.getPosX() != o2.getPosX()) {
                return o2.getPosX() - o1.getPosX();
            }
            return o2.getSpriteNumber() - o1.getSpriteNumber();
        });


        for (Sprite sprite : spritesToDraw) {

            int spriteRow = line - sprite.getPosY();
            byte tileNumber = sprite.getTileNumber();

            if (gpuRegisters.spritesSizeMode != 0x00) {    //In 8x16 mode, always specifies the top one (lower bit is ignored)
                tileNumber = (byte) (tileNumber & 0xFE);
            }

            int[] spriteIndexes = readSpriteLine(tileNumber, spriteRow, sprite.hFlip(), sprite.vFlip());

            int spriteStartX = sprite.getPosX() >= 0 ? 0 : -sprite.getPosX();
            int spriteLength = getSpriteLength(sprite);
            if (spriteLength <= 0) {
                continue;
            }
            byte[] spritePriority = new byte[8];
            byte[] spritePalette = new byte[8];

            Arrays.fill(spritePriority, sprite.getPriority());
            Arrays.fill(spritePalette, sprite.getPalette());
            int drawStartX = Math.max(sprite.getPosX(), 0);

            copyRespectingTransparency(
                    spriteIndexes, spritePalette, spritePriority,
                    spritesIndexes, spritePalletIdxs, bgPriority,
                    spriteStartX, drawStartX, spriteLength);
        }

        for (int i = 0; i < spritesRenderInfo.length; i++) {
            spritesRenderInfo[i] = new SpriteRenderInfo(spritesIndexes[i], bgPriority[i], spritePalletIdxs[i]);
        }

        return spritesRenderInfo;
    }

    private void copyRespectingTransparency(int[] srcIndexes, byte[] srcPalette, byte[] srcPriority,
                                            int[] dstIndexes, byte[] dstPalette, byte[] dstPriority,
                                            int srcPos, int dstPos, int length) {
        int position = 0;
        while (position < length) {
            int newIndex = srcIndexes[srcPos + position];

            if (newIndex != 0) {   //Copy new data
                dstIndexes[dstPos + position] = srcIndexes[srcPos + position];
                dstPalette[dstPos + position] = srcPalette[srcPos + position];
                dstPriority[dstPos + position] = srcPriority[srcPos + position];
            }

            position++;
        }

    }

    private int getSpriteLength(Sprite sprite) {
        if (sprite.getPosX() + 7 > 159) { //Out of screen in the right
            return 8 - ((sprite.getPosX() + 7) - 159);
        } else if (sprite.getPosX() < 0) {
            return Math.max(0, 8 + sprite.getPosX());
        } else {
            return 8;
        }
    }

    private int[] readSpriteLine(byte tileNumber, int spriteRow, boolean hFlip, boolean vFlip) {
        //Need to read 8 pixels
        char tileAddress = (char) (0x8000 + 16 * (tileNumber & 0xFF));
        int spritesHeight = gpuRegisters.spritesSizeMode == 0x00 ? 8 : 16;
        return TileUtils.getRowOfTileIndexes(memory, tileAddress, spriteRow, hFlip, vFlip, spritesHeight);
    }


}
