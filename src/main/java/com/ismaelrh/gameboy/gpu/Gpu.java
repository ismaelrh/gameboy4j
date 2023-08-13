package com.ismaelrh.gameboy.gpu;

import com.ismaelrh.gameboy.gpu.lcd.Lcd;
import com.ismaelrh.gameboy.cpu.memory.MMIODevice;
import com.ismaelrh.gameboy.cpu.memory.Memory;

public class Gpu extends MMIODevice {

    private final Memory memory;
    private final Lcd lcd;

    public Gpu(Memory memory, Lcd lcd) {
        super((char) 0xFF40, (char) 0xFF79);
        this.memory = memory;
        this.lcd = lcd;
    }

    private int mode = 2;
    private int currentClock = 0;
    private int line = 0;

    private boolean lcdEnabled = true;
    private int tileMap = 0;    //0 or 1
    private int tileSet = 0;    //0 or 1

    private final int[] pallete = new int[4];

    private int gpuIRQ = 0;

    private final int OAM_MODE = 2;
    private final int VRAM_MODE = 3;
    private final int HBLANK_MODE = 0;
    private final int VBLANK_MODE = 1;
    private final char[] TILEMAP_START_ADDRESSES = new char[]{0x9800, 0x9C00};
    private final char[] TILESET_START_ADDRESSES = new char[]{0x8800, 0x8000};


    private final int OAM_CYCLES = 80;
    private final int VRAM_CYCLES = 172;
    private final int HBLANK_CYCLES = 204;
    private final int VBLANK_CYCLES = 456;

    private final char LCD_CONTROL_ADDRRESS = (char) 0xFF40;
    private byte lcd_control = (byte) 0x00;

    private final char LCD_STAT_ADDRESS = (char) 0xFF41;
    private byte lcd_stat = (byte) 0x80; //8th bit is always set

    private final char LCD_SCROLL_Y_ADDRESS = (char) 0xFF42;
    private byte scrollY = (byte) 0x00;

    private final char LCD_SCROLL_X_ADDRESS = (char) 0xFF43;
    private byte scrollX = (byte) 0x00;

    private final char LCD_LY_ADDRESS = (char) 0xFF44;
    private byte lcd_ly = (byte) 0x00;

    private final char LCD_LYC_ADDRESS = (char) 0xFF45;
    private byte lcd_lyc = (byte) 0x00;

    private final char LCD_BG_PALLETE_ADDRESS = (char) 0xFF47;
    private byte pallete_reg = (byte) 0x00;

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
                updateTileMap();
                updateTileSet();
                break;
            case LCD_BG_PALLETE_ADDRESS:
                pallete_reg = data;
                updatePallete();
                break;
            case LCD_SCROLL_Y_ADDRESS:
                scrollY = data;
                break;
            case LCD_SCROLL_X_ADDRESS:
                scrollX = data;
                break;
            default:
                //Any other is not written
        }
    }

    private void updatePallete() {
        for (int i = 0; i < 4; i++) {
            int value = ((pallete_reg & (3 << 2 * i)) >> 2 * i) & 0xFF;
            pallete[i] = Lcd.RGB_COLORS[value];
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
                return pallete_reg;
            case LCD_SCROLL_Y_ADDRESS:
                return scrollY;
            case LCD_SCROLL_X_ADDRESS:
                return scrollX;
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

    //bit 3
    private void updateTileMap() {
        if ((lcd_control & 0x08) != 0) {
            tileMap = 1;
        } else {
            tileMap = 0;
        }
    }

    //bit 4
    private void updateTileSet() {
        if ((lcd_control & 0x10) != 0) {
            tileSet = 1;
        } else {
            tileSet = 0;
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

    private void doScanline() {
        //Get the line I'm drawing
        int drawingLine = line;

        //Need to get the tile row and the pixel row we are going to draw,
        //according to scrollY and drawingLine
        int fullMapY = drawingLine + (scrollY & 0xFF);
        int verticalTilePos = fullMapY / 8;
        int tileRow = fullMapY - verticalTilePos * 8;

        if (verticalTilePos >= 32) {
            verticalTilePos -= 32; //Wrap
        }

        //Need to read the line
        int[] lineIndexes = readLine(verticalTilePos, tileRow, scrollX);

        //Transform indexes to emulator colors
        int[] lineColors = TileUtils.applyPalleteToIndexes(lineIndexes, pallete);

        //Push data to screen!
        for (int lineColor : lineColors) {
            lcd.pushPixel(lineColor);
        }

    }

    private int[] readLine(int verticalTilePos, int tileRow, int scrollX) {
        //Need to read 160 pixels (5 tiles, but can be in the middle)
        int[] line = new int[160];

        int horizontalTilePos = (scrollX & 0xFF) / 8;
        int pixelsOffset = (scrollX & 0xFF) - horizontalTilePos * 8;
        int readPixels = 0;

        while (readPixels < 160) {
            char tileAddress = getTileAddress(verticalTilePos, horizontalTilePos);
            int[] data = TileUtils.getRowOfTileIndexes(memory, tileAddress, tileRow);
            int startIdx = 0;
            int length = 8;
            if (readPixels == 0) {
                startIdx = pixelsOffset;
                length = 8 - pixelsOffset;
            }
            if (readPixels + 8 > 160) {
                length = 160 - readPixels;
            }

            try {
                System.arraycopy(data, startIdx, line, readPixels, length);

            } catch (Exception e) {
                int a = 2;
            }
            readPixels += length;
            horizontalTilePos += 1;
        }
        return line;
    }

    private char getTileAddress(int verticalTilePos, int horizontalTilePos) {
        //Normalized, from 0 to 255
        int tileIndex = getRelativeTileIndex(verticalTilePos, horizontalTilePos);

        //Each tile is 16bytes long, so retrieve the corresponding tile
        return (char) (TILESET_START_ADDRESSES[tileSet] + tileIndex * 16);
    }

    //Normalized, from 0 to 255
    private int getRelativeTileIndex(int verticalTilePos, int horizontalTilePos) {
        char mapAddress = TILEMAP_START_ADDRESSES[tileMap];
        int tileIndexAddress = 32 * verticalTilePos + horizontalTilePos;
        byte relativeIndex = memory.read((char) (mapAddress + tileIndexAddress));
        if (tileSet == 1) {
            return relativeIndex & 0xFF;
        } else {
            return (relativeIndex + 128) & 0xFF;
        }
    }

    public int[] getPallete() {
        return pallete;
    }

    public byte getScrollY() {
        return scrollY;
    }
}
