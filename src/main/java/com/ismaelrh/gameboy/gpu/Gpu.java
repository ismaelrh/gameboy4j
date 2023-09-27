package com.ismaelrh.gameboy.gpu;

import com.ismaelrh.gameboy.gpu.lcd.Lcd;
import com.ismaelrh.gameboy.cpu.memory.MMIODevice;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.gpu.sprites.Sprite;
import com.ismaelrh.gameboy.gpu.sprites.SpriteRenderInfo;
import com.ismaelrh.gameboy.gpu.sprites.SpritesInfo;

import java.util.Arrays;

public class Gpu extends MMIODevice {

    private final static int NO_PIXEL_INDEX = -1;
    private final Memory memory;

    private final SpritesInfo spritesInfo;
    private final Lcd lcd;

    public Gpu(Memory memory, Lcd lcd) {
        super((char) 0xFF40, (char) 0xFF79);
        this.memory = memory;
        this.lcd = lcd;
        this.spritesInfo = new SpritesInfo();
        this.memory.addInterceptor(this.spritesInfo);
    }

    private int mode = 2;
    private int currentClock = 0;
    private int line = 0;

    private boolean lcdEnabled = true;
    private int bgTileMap = 0;    //0 or 1
    private int tileAddressingMode = 0;    //0 -> Uses 8800 + signed method. 1 -> Uses 8000 + unsigned method.

    private boolean bgWindowEnabled = false;
    private boolean windowEnabled = false;  //0xFF40, bit 5
    private int windowTileMap = 0;

    private final char LCD_WY_ADDRESS = (char) 0xFF4A;
    private byte wY = 0x00;    //0xFF4A
    private final char LCD_WX_ADDRESS = (char) 0xFF4B;
    private byte wX = 0x00;    //0xFF4B

    //Internal window line counter
    private byte windowY = 0x00;

    private boolean spritesEnabled = false;

    private int spritesSizeMode = 0;


    private final int[] bgPalette = new int[4];

    private final int[] spritePalette0 = new int[4];
    private final int[] spritePalette1 = new int[4];
    private final int[][] spritePalletes = new int[][]{spritePalette0, spritePalette1};

    private int gpuIRQ = 0;

    private final int OAM_MODE = 2;
    private final int VRAM_MODE = 3;
    private final int HBLANK_MODE = 0;
    private final int VBLANK_MODE = 1;
    private final char[] TILEMAP_START_ADDRESSES = new char[]{0x9800, 0x9C00};
    private final char[] TILE_ADDRESSING_MODES_START = new char[]{0x8800, 0x8000};


    private final int OAM_CYCLES = 80;
    private final int VRAM_CYCLES = 172;
    private final int HBLANK_CYCLES = 204;
    private final int VBLANK_CYCLES = 456;

    private final char LCD_CONTROL_ADDRRESS = (char) 0xFF40;
    private byte lcd_control = (byte) 0x91;

    private final char LCD_STAT_ADDRESS = (char) 0xFF41;
    private byte lcd_stat = (byte) 0x87; //8th bit is always set

    private final char LCD_SCROLL_Y_ADDRESS = (char) 0xFF42;
    private byte scrollY = (byte) 0x00;

    private final char LCD_SCROLL_X_ADDRESS = (char) 0xFF43;
    private byte scrollX = (byte) 0x00;

    private final char LCD_LY_ADDRESS = (char) 0xFF44;
    private byte lcd_ly = (byte) 0x00;

    private final char LCD_LYC_ADDRESS = (char) 0xFF45;
    private byte lcd_lyc = (byte) 0x00;

    private final char LCD_BG_PALLETE_ADDRESS = (char) 0xFF47;

    private final char LCD_SPRITE_PALLETE_0_ADDRESS = (char) 0xFF48;
    private final char LCD_SPRITE_PALLETE_1_ADDRESS = (char) 0xFF49;


    private byte bg_pallete_reg = (byte) 0x00;

    private byte sprite_pallete_0_reg = (byte) 0x00;
    private byte sprite_pallete_1_reg = (byte) 0x00;

    @Override
    public void onWrite(char address, byte data) {
        switch (address) {
            case LCD_STAT_ADDRESS:
                //Only can write bits 3 to 6
                byte dataToWrite = (byte) (data & 0x78); //Keep only bits 3-6 from input
                //Remove bits 3-6 from data and write the new data
                this.lcd_stat = (byte) (((this.lcd_stat & 0x87) | dataToWrite) & 0xFF);
                break;
            case LCD_LYC_ADDRESS:
                this.lcd_lyc = data;
                checkForStatIRQ();
                break;
            case LCD_CONTROL_ADDRRESS:
                this.lcd_control = data;
                checkLcdEnabled();
                checkBgWindowEnabled();
                checkWindowEnabled();
                checkSpritesEnabled();
                checkSpritesSizeMode();
                updateBgTileMap();
                updateWindowTileMap();
                updateTileAddressingMode();
                break;
            case LCD_BG_PALLETE_ADDRESS:
                bg_pallete_reg = data;
                updateBgPalettes();
                break;
            case LCD_SPRITE_PALLETE_0_ADDRESS:
                sprite_pallete_0_reg = data;
                updateSpritesPalettes();
                break;
            case LCD_SPRITE_PALLETE_1_ADDRESS:
                sprite_pallete_1_reg = data;
                updateSpritesPalettes();
                break;
            case LCD_SCROLL_Y_ADDRESS:
                scrollY = data;
                break;
            case LCD_SCROLL_X_ADDRESS:
                scrollX = data;
                break;
            case LCD_WY_ADDRESS:
                wY = data;
                break;
            case LCD_WX_ADDRESS:
                wX = data;
                break;
            default:
                //Any other is not written
        }
    }

    private void updateBgPalettes() {
        for (int i = 0; i < 4; i++) {
            int value = ((bg_pallete_reg & (3 << 2 * i)) >> 2 * i) & 0xFF;
            bgPalette[i] = Lcd.BG_COLORS[value];
        }
    }

    private void updateSpritesPalettes() {

        //0 is always transparent
        spritePalette0[0] = Lcd.COLOR_0_TRANSPARENT;
        spritePalette1[0] = Lcd.COLOR_0_TRANSPARENT;

        for (int i = 1; i < 4; i++) {
            int value = ((sprite_pallete_0_reg & (3 << 2 * i)) >> 2 * i) & 0xFF;
            spritePalette0[i] = Lcd.SPRITE_COLORS[value];
        }
        for (int i = 1; i < 4; i++) {
            int value = ((sprite_pallete_1_reg & (3 << 2 * i)) >> 2 * i) & 0xFF;
            spritePalette1[i] = Lcd.SPRITE_COLORS[value];
        }
    }


    @Override
    public byte onRead(char address) {
        switch (address) {
            case LCD_STAT_ADDRESS:  //Most significant bit is always 1
                return (byte) (lcd_stat | 0x80);
            case LCD_LY_ADDRESS:
                return lcd_ly;
            case LCD_LYC_ADDRESS:
                return lcd_lyc;
            case LCD_CONTROL_ADDRRESS:
                return lcd_control;
            case LCD_BG_PALLETE_ADDRESS:
                return bg_pallete_reg;
            case LCD_SPRITE_PALLETE_0_ADDRESS:
                return sprite_pallete_0_reg;
            case LCD_SPRITE_PALLETE_1_ADDRESS:
                return sprite_pallete_1_reg;
            case LCD_SCROLL_Y_ADDRESS:
                return scrollY;
            case LCD_SCROLL_X_ADDRESS:
                return scrollX;
            case LCD_WY_ADDRESS:
                return wY;
            case LCD_WX_ADDRESS:
                return wX;
            default:
                return (byte) 0xFF;
        }
    }

    /**
     * Mode 2: 80 cycles, 0AM access
     * Mode 3: 172 cycles, VRAM access
     * Mode 0: 204 cycles, HBlank
     * Mode 1: Vblank mode,  When 143 lines, enter into vblank mode for 10 lines
     */
    public void tick(int cycles) {
        if (!lcdEnabled) {  //On LCD enabled, this does nothing
            return;
        }
        currentClock += cycles;
        switch (mode) {
            case OAM_MODE:
                oamMode();
                break;
            case VRAM_MODE:
                vramMode();
                break;
            case HBLANK_MODE:
                hblankMode();
                break;
            case VBLANK_MODE:
                vblankMode();
                break;
        }
    }

    private void oamMode() {
        if (currentClock >= OAM_CYCLES) {
            setPpuMode(VRAM_MODE);
            currentClock -= OAM_CYCLES;
        }
    }

    private void vramMode() {
        if (currentClock >= VRAM_CYCLES) {
            setPpuMode(HBLANK_MODE);
            currentClock -= VRAM_CYCLES;
            doScanline();
            //End of scanline, write it to framebuffer
        }
    }


    private void hblankMode() {

        if (currentClock >= HBLANK_CYCLES) {

            currentClock -= HBLANK_CYCLES;

            setLine(line + 1);

            if (line == 144) {  //enter vblank if 143 lines
                setPpuMode(VBLANK_MODE);
                //TODO: Display when entering this mode
            } else { //If not 143 lines, return to OAM mode
                setPpuMode(OAM_MODE);
            }
        }

    }

    private void vblankMode() {

        //Fire Blank interruption, a new frame was drawn
        if (currentClock >= VBLANK_CYCLES) { //One vblank line
            currentClock -= VBLANK_CYCLES;
            setLine(line + 1);
            if (line == 153) {    //10 lines
                setLine(0);
                setPpuMode(OAM_MODE);
            }
        }
    }

    private void setLine(int number) {
        line = number;
        lcd_ly = (byte) ((byte) number & 0xFF);

        lcd_stat = (byte) (lcd_stat & 0xFB); //Clear 2nd byte (coincidence)

        if (lcd_ly == lcd_lyc) {
            //Set 2nd register of STAT if same
            lcd_stat = (byte) ((lcd_stat | 0x04) & 0xFF);
            checkForStatIRQ();
        }
    }

    private void setPpuMode(int newMode) {

        mode = newMode; //Change internal mode

        //Change in STAT register (last two bits)
        byte byteMode = (byte) ((byte) mode & 0xFF);
        lcd_stat = (byte) ((lcd_stat & 0xFC) | byteMode);

        checkForStatIRQ();

        if (mode == VBLANK_MODE) {
            windowY = 0x00; //Reset every VBLANK
            lcd.flush();
            memory.fireVBlankInterruption();
        }
    }

    /**
     * Done when:
     * starting up LCD
     * changing LYC
     * changing line
     */
    private void checkForStatIRQ() {
        if (lcdEnabled) {
            if (gpuIRQ == 0 && isTriggerConditionMeet()) {
                gpuIRQ = 1;
                memory.fireLcdInterruption();
            }
            if (!isTriggerConditionMeet()) {
                gpuIRQ = 0;
            }
        }
    }

    private boolean isTriggerConditionMeet() {
        //There is an LCD interruption condition met
        return (mode == HBLANK_MODE && (byte) (lcd_stat & 0x08) != 0) ||
                (mode == VBLANK_MODE && (byte) (lcd_stat & 0x10) != 0) ||
                (mode == OAM_MODE && (byte) (lcd_stat & 0x20) != 0) ||
                (lcd_ly == lcd_lyc && (byte) (lcd_stat & 0x40) != 0);
    }


    private void checkLcdEnabled() {
        if ((lcd_control & 0x80) != 0) {
            enableLcd();
        } else {
            disableLcd();
        }
    }

    private void checkBgWindowEnabled() {
        bgWindowEnabled = (lcd_control & 0x01) != 0;
    }

    private void checkWindowEnabled() {
        windowEnabled = (lcd_control & 0x20) != 0;
    }

    private void checkSpritesEnabled() {
        spritesEnabled = (lcd_control & 0x02) != 0;
    }

    private void checkSpritesSizeMode() {
        spritesSizeMode = (lcd_control & 0x04) >> 2;
    }

    //bit 3
    private void updateBgTileMap() {
        if ((lcd_control & 0x08) != 0) {
            bgTileMap = 1;
        } else {
            bgTileMap = 0;
        }
    }

    private void updateWindowTileMap() {
        if ((lcd_control & 0x40) != 0) {
            windowTileMap = 1;
        } else {
            windowTileMap = 0;
        }
    }

    //bit 4
    private void updateTileAddressingMode() {
        if ((lcd_control & 0x10) != 0) {
            tileAddressingMode = 1;
        } else {
            tileAddressingMode = 0;    //tileSet = 0, tileMap = 1
        }
    }

    private void disableLcd() {
        if (lcdEnabled) {
            lcd.disableLcd();
            lcdEnabled = false;
            setPpuMode(0);
            currentClock = 0;
            line = 0;
        }

    }

    private void enableLcd() {
        if (!lcdEnabled) {
            lcd.enableLcd();
            lcdEnabled = true;
        }

    }

    private int[] getBackgroundIndexes(int drawingLine) {
        //Need to get the tile row and the pixel row we are going to draw,
        //according to scrollY and drawingLine
        int fullMapY = drawingLine + (scrollY & 0xFF);  //Line of the whole view to paint
        int verticalTilePos = fullMapY / 8; //Tile to draw
        int tileRow = fullMapY - verticalTilePos * 8;   //#row inside the tile (0..7)

        if (verticalTilePos >= 32) {
            verticalTilePos -= 32; //Wrap
        }

        //Need to read the line (# of tile, row of the tile, x scroll)
        return readBackgroundLine(verticalTilePos, tileRow, scrollX);
    }

    private int[] getWindowIndexes(int drawingLine) {

        boolean windowReached = drawingLine - wY >= 0;
        int tileMapRow = windowY; //Row of tilemap to draw. If negative, we do not draw.
        int verticalTilePos = tileMapRow / 8; //Tile to draw. Never will be >= 32.
        int tileRow = tileMapRow - verticalTilePos * 8;   //#row inside the tile (0..7)

        //if WY > 144 or WX > 166 or Y < 0, we do not draw
        if (Byte.compareUnsigned(wY, (byte) 144) > 0 || Byte.compareUnsigned(wX, (byte) 166) > 0 || !windowReached) {
            //TODO: CONSTANT
            int[] whiteLine = new int[160];
            Arrays.fill(whiteLine, -1);
            return whiteLine;
        }

        return readWindowLine(verticalTilePos, tileRow, wX);

    }

    private SpriteRenderInfo[] getSpriteRenderInfo(int line) {

        SpriteRenderInfo[] spritesRenderInfo = new SpriteRenderInfo[160];
        int[] spritesIndexes = new int[160];
        byte[] bgPriority = new byte[160];
        byte[] spritePalletIdxs = new byte[160];


        Sprite[] spritesToDraw = spritesInfo.getSpritesToDrawOnLine(line, spritesSizeMode);

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

            if (spritesSizeMode != 0x00) {    //In 8x16 mode, always specifies the top one (lower bit is ignored)
                tileNumber = (byte) (tileNumber & 0xFE);
            }

            int[] spriteIndexes = readSpriteLine(tileNumber, spriteRow, sprite.hFlip(), sprite.vFlip());

            int spriteStartX = sprite.getPosX() >= 0 ? 0 : -sprite.getPosX();
            int spriteLength = (sprite.getPosX() + 7) <= 159 ? 8 : 8 - ((sprite.getPosX() + 7) - 159);
            byte[] spritePriority = new byte[spriteLength];
            byte[] spritePalette = new byte[spriteLength];

            Arrays.fill(spritePriority, sprite.getPriority());
            Arrays.fill(spritePalette, sprite.getPalette());
            int drawStartX = Math.max(sprite.getPosX(), 0);
            if (spriteStartX + spriteLength > 0) {
                System.arraycopy(spriteIndexes, spriteStartX, spritesIndexes, drawStartX, spriteLength);
                System.arraycopy(spritePalette, spriteStartX, spritePalletIdxs, drawStartX, spriteLength);
                System.arraycopy(spritePriority, spriteStartX, bgPriority, drawStartX, spriteLength);
            }
        }

        for (int i = 0; i < spritesRenderInfo.length; i++) {
            spritesRenderInfo[i] = new SpriteRenderInfo(spritesIndexes[i], bgPriority[i], spritePalletIdxs[i]);
        }

        return spritesRenderInfo;
    }


    private void doScanline() {

        int[] backgroundIndexes = getBackgroundIndexes(line);
        int[] windowIndexes = getWindowIndexes(line);
        SpriteRenderInfo[] spritesRenderInfo = getSpriteRenderInfo(line);

        int[] lineToDraw = mergeLines(backgroundIndexes, windowIndexes, spritesRenderInfo);

        //Push data to screen!
        for (int lineColor : lineToDraw) {
            lcd.pushPixel(lineColor);
        }
    }


    /**
     * If, during the shifting process, both the Background and the Sprite FIFO contain at least one pixel, they are both shifted out and compared as follows:
     * 1) If the color number of the Sprite Pixel is 0, the Background Pixel is pushed to the LCD.
     * 2) If the BG-to-OBJ-Priority bit is 1 and the color number of the Background Pixel is anything other than 0, the Background Pixel is pushed to the LCD.
     * 3) If none of the above conditions apply, the Sprite Pixel is pushed to the LCD.
     */
    private int[] mergeLines(int[] bgIndexes, int[] windowIndexes, SpriteRenderInfo[] spriteInfo) {

        int[] mergedLine = new int[bgIndexes.length];

        for (int x = 0; x < mergedLine.length; x++) {

            int bgIndex = windowIndexes[x] == -1 ? bgIndexes[x] : windowIndexes[x];
            int bgColor = TileUtils.bgApplyPaletteToIndex(bgIndex, bgPalette);
            mergedLine[x] = bgColor;

            if (spritesEnabled) {

                int spriteIdx = spriteInfo[x].getIndex();
                int[] spritePalette = spritePalletes[spriteInfo[x].getPaletteIdx()];
                int spriteColor = TileUtils.spriteApplyPaletteToIndex(spriteIdx, spritePalette);
                byte spriteBgPriority = spriteInfo[x].getBgPriority();

                if (spriteColor == 0) {    //If index 0, continue
                    continue;
                }

                //Priority 0 = Sprite on top of BG
                //Priority 1 = Only visible if background is color 0, or background is disabled
                if (spriteBgPriority == 0 || !bgWindowEnabled || bgColor == Lcd.COLOR_0_WHITE) {
                    mergedLine[x] = TileUtils.spriteApplyPaletteToIndex(spriteIdx, spritePalette);
                }

            }

        }

        return mergedLine;
    }


    private int[] readSpriteLine(byte tileNumber, int spriteRow, boolean hFlip, boolean vFlip) {
        //Need to read 8 pixels
        char tileAddress = (char) (0x8000 + 16 * (tileNumber & 0xFF));
        int spritesHeight = spritesSizeMode == 0x00 ? 8 : 16;
        return TileUtils.getRowOfTileIndexes(memory, tileAddress, spriteRow, hFlip, vFlip, spritesHeight);
    }

    private int[] readWindowLine(int verticalTilePos, int tileRow, int windowX) {

        int windowRealX = windowX - 7;

        int[] line = new int[160];
        Arrays.fill(line, -1);

        if (!bgWindowEnabled || !windowEnabled) {
            return line;    //All empty
        }


        int currentScreenPixel = Math.max(windowRealX, 0); //if < 0, we start drawing at 0 in screen.
        int currentTileLinePixel = 0;

        //In this scanline, there is window to draw, so we increase.
        if (currentScreenPixel < 160) {
            windowY += 1;
        }

        while (currentScreenPixel < 160) {
            int remainingPixels = 160 - currentScreenPixel;
            int dataLength = Math.min(remainingPixels, 8);
            int horizontalTilePos = currentTileLinePixel / 8;
            char tileAddress = getTileAddress(verticalTilePos, horizontalTilePos, true);
            int[] data = TileUtils.getRowOfTileIndexes(memory, tileAddress, tileRow, false, false);
            System.arraycopy(data, 0, line, currentScreenPixel, dataLength);
            currentScreenPixel += dataLength;
            currentTileLinePixel += dataLength;
        }

        return line;
    }

    private int[] readBackgroundLine(int verticalTilePos, int tileRow, int scrollX) {

        //Need to read 160 pixels (5 tiles, but can be in the middle)
        int[] line = new int[160];

        if (!bgWindowEnabled) {
            Arrays.fill(line, -1);
            return line;
        }

        int horizontalTilePos = (scrollX & 0xFF) / 8;   //Number of tile (in row, horizontally)
        int xOffsetInsideFirstTile = (scrollX & 0xFF) - horizontalTilePos * 8;    //X offset inside tile
        int readPixels = 0;


        while (readPixels < 160) {

            char tileAddress = getTileAddress(verticalTilePos, horizontalTilePos, false);
            int[] data = TileUtils.getRowOfTileIndexes(memory, tileAddress, tileRow, false, false);

            int startIdx = 0;
            int length = 8;

            //Cut the beginning of the screen
            if (readPixels == 0) {
                startIdx = xOffsetInsideFirstTile;
                length = 8 - xOffsetInsideFirstTile;
            }

            //Cut the end of the screen
            if (readPixels + 8 > 160) {
                length = 160 - readPixels;
            }

            System.arraycopy(data, startIdx, line, readPixels, length);
            readPixels += length;
            horizontalTilePos = (horizontalTilePos + 1) % 32;   //Wrap horizontal tile
        }
        return line;
    }

    private char getTileAddress(int verticalTilePos, int horizontalTilePos, boolean isWindow) {

        //Normalized, from 0 to 255
        int tileIndex = getRelativeTileIndex(verticalTilePos, horizontalTilePos, isWindow);

        //Each tile is 16bytes long, so retrieve the corresponding tile
        return (char) (TILE_ADDRESSING_MODES_START[tileAddressingMode] + tileIndex * 16);
    }
    //ERROR ON //tileSet = 0, tileMap = 1

    //Normalized, from 0 to 255
    private int getRelativeTileIndex(int verticalTilePos, int horizontalTilePos, boolean isWindow) {

        int tileMap = isWindow ? windowTileMap : bgTileMap;

        char bgMapAddress = TILEMAP_START_ADDRESSES[tileMap];

        //The position of tile index to read inside the bgMap
        int tileIndexAddress = 32 * verticalTilePos + horizontalTilePos;

        byte relativeIndex = memory.read((char) (bgMapAddress + tileIndexAddress));

        if (tileAddressingMode == 1) {  //Unsigned method
            return relativeIndex & 0xFF;    //Remove all signed.
        } else {
            return (relativeIndex + 128) & 0xFF;
        }
    }

    public int[] getBgPalette() {
        return bgPalette;
    }

    public byte getScrollY() {
        return scrollY;
    }
}
