package com.ismaelrh.gameboy.gpu;

import com.ismaelrh.gameboy.cpu.memory.MMIODevice;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.gpu.lcd.Lcd;
import com.ismaelrh.gameboy.gpu.render.BackgroundRenderer;
import com.ismaelrh.gameboy.gpu.render.SpriteRenderer;
import com.ismaelrh.gameboy.gpu.render.WindowRenderer;
import com.ismaelrh.gameboy.gpu.sprites.SpriteRenderInfo;
import com.ismaelrh.gameboy.gpu.sprites.SpritesInfo;
import com.ismaelrh.gameboy.gpu.tiles.TileUtils;

public class Gpu extends MMIODevice {
    //Addresses
    public static final char OAM_START_ADDRESS = 0xFE00;
    public static final char OAM_END_ADDRESS = 0xFE9F;
    public static final char[] TILEMAP_START_ADDRESSES = new char[]{0x9800, 0x9C00};
    public static final char[] TILE_ADDRESSING_MODES_START_ADDRESSES = new char[]{0x8800, 0x8000};

    //Modes and its cycles
    private final int OAM_MODE = 2;
    private final int OAM_CYCLES = 80;
    private final int VRAM_MODE = 3;
    private final int VRAM_CYCLES = 172;
    private final int HBLANK_MODE = 0;
    private final int HBLANK_CYCLES = 204;
    private final int VBLANK_MODE = 1;
    private final int VBLANK_CYCLES = 456;


    private final Memory memory;
    private final SpritesInfo spritesInfo;
    private final Lcd lcd;
    private final GpuRegisters gpuRegisters;
    private final GpuRegistersMapping gpuRegistersMapping;

    //Renderers
    private final BackgroundRenderer backgroundRenderer;
    private final WindowRenderer windowRenderer;
    private final SpriteRenderer spriteRenderer;

    public Gpu(Memory memory, Lcd lcd) {
        super((char) 0xFF40, (char) 0xFF79);
        this.memory = memory;
        this.gpuRegisters = new GpuRegisters();
        this.lcd = lcd;
        this.spritesInfo = new SpritesInfo();
        this.memory.addInterceptor(this.spritesInfo);
        this.backgroundRenderer = new BackgroundRenderer(this.memory, this.gpuRegisters);
        this.windowRenderer = new WindowRenderer(this.memory, this.gpuRegisters);
        this.spriteRenderer = new SpriteRenderer(this.memory, this.gpuRegisters, this.spritesInfo);
        this.gpuRegistersMapping = new GpuRegistersMapping(this, gpuRegisters);
    }

    @Override
    public void onWrite(char address, byte data) {
        this.gpuRegistersMapping.onWrite(address, data);
    }

    @Override
    public byte onRead(char address) {
        return this.gpuRegistersMapping.onRead(address);
    }

    /**
     * Mode 2: 80 cycles, 0AM access
     * Mode 3: 172 cycles, VRAM access
     * Mode 0: 204 cycles, HBlank
     * Mode 1: Vblank mode,  When 143 lines, enter into vblank mode for 10 lines
     */
    public void tick(int cycles) {
        if (!gpuRegisters.lcdEnabled) {  //On LCD enabled, this does nothing
            return;
        }
        gpuRegisters.currentClock += cycles;
        switch (gpuRegisters.mode) {
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
        if (gpuRegisters.currentClock >= OAM_CYCLES) {
            setPpuMode(VRAM_MODE);
            gpuRegisters.currentClock -= OAM_CYCLES;
        }
    }

    private void vramMode() {
        if (gpuRegisters.currentClock >= VRAM_CYCLES) {
            setPpuMode(HBLANK_MODE);
            gpuRegisters.currentClock -= VRAM_CYCLES;
            doScanline();
            //End of scanline, write it to framebuffer
        }
    }

    private void hblankMode() {
        if (gpuRegisters.currentClock >= HBLANK_CYCLES) {

            gpuRegisters.currentClock -= HBLANK_CYCLES;

            setLine(gpuRegisters.line + 1);

            if (gpuRegisters.line == 144) {  //enter vblank if 143 lines
                setPpuMode(VBLANK_MODE);
            } else { //If not 143 lines, return to OAM mode
                setPpuMode(OAM_MODE);
            }
        }
    }

    private void vblankMode() {
        //Fire Blank interruption, a new frame was drawn
        if (gpuRegisters.currentClock >= VBLANK_CYCLES) { //One vblank line
            gpuRegisters.currentClock -= VBLANK_CYCLES;
            setLine(gpuRegisters.line + 1);
            if (gpuRegisters.line == 153) {    //10 lines
                setLine(0);
                setPpuMode(OAM_MODE);
            }
        }
    }

    private void setLine(int number) {
        gpuRegisters.line = number;
        gpuRegisters.lcd_ly = (byte) ((byte) number & 0xFF);

        gpuRegisters.lcd_stat = (byte) (gpuRegisters.lcd_stat & 0xFB); //Clear 2nd byte (coincidence)

        if (gpuRegisters.lcd_ly == gpuRegisters.lcd_lyc) {
            //Set 2nd register of STAT if same
            gpuRegisters.lcd_stat = (byte) ((gpuRegisters.lcd_stat | 0x04) & 0xFF);
            checkForStatIRQ();
        }
    }

    private void setPpuMode(int newMode) {
        gpuRegisters.mode = newMode; //Change internal mode

        //Change in STAT register (last two bits)
        byte byteMode = (byte) ((byte) gpuRegisters.mode & 0xFF);
        gpuRegisters.lcd_stat = (byte) ((gpuRegisters.lcd_stat & 0xFC) | byteMode);

        checkForStatIRQ();

        if (gpuRegisters.mode == VBLANK_MODE) {
            gpuRegisters.windowY = 0x00; //Reset every VBLANK
            lcd.flush();
            memory.fireVBlankInterruption();
        }
    }

    protected void disableLcd() {
        if (gpuRegisters.lcdEnabled) {
            lcd.disableLcd();
            gpuRegisters.lcdEnabled = false;
            setPpuMode(0);
            gpuRegisters.currentClock = 0;
            gpuRegisters.line = 0;
        }

    }

    protected void enableLcd() {
        if (!gpuRegisters.lcdEnabled) {
            lcd.enableLcd();
            gpuRegisters.lcdEnabled = true;
        }
    }

    /**
     * Done when:
     * starting up LCD
     * changing LYC
     * changing line
     */
    protected void checkForStatIRQ() {
        if (gpuRegisters.lcdEnabled) {
            if (gpuRegisters.gpuIRQ == 0 && isTriggerConditionMeet()) {
                gpuRegisters.gpuIRQ = 1;
                memory.fireLcdInterruption();
            }
            if (!isTriggerConditionMeet()) {
                gpuRegisters.gpuIRQ = 0;
            }
        }
    }

    private boolean isTriggerConditionMeet() {
        //There is an LCD interruption condition met
        return (gpuRegisters.mode == HBLANK_MODE && (byte) (gpuRegisters.lcd_stat & 0x08) != 0) ||
                (gpuRegisters.mode == VBLANK_MODE && (byte) (gpuRegisters.lcd_stat & 0x10) != 0) ||
                (gpuRegisters.mode == OAM_MODE && (byte) (gpuRegisters.lcd_stat & 0x20) != 0) ||
                (gpuRegisters.lcd_ly == gpuRegisters.lcd_lyc && (byte) (gpuRegisters.lcd_stat & 0x40) != 0);
    }


    private void doScanline() {
        int[] backgroundIndexes = backgroundRenderer.getBackgroundIndexes(gpuRegisters.line);
        int[] windowIndexes = windowRenderer.getWindowIndexes(gpuRegisters.line);
        SpriteRenderInfo[] spritesRenderInfo = spriteRenderer.getSpriteRenderInfo(gpuRegisters.line);

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
            int bgColor = TileUtils.bgApplyPaletteToIndex(bgIndex, gpuRegisters.bgPalette);
            mergedLine[x] = bgColor;

            if (gpuRegisters.spritesEnabled) {

                int spriteIdx = spriteInfo[x].getIndex();
                int[] spritePalette = gpuRegisters.spritePalletes[spriteInfo[x].getPaletteIdx()];
                int spriteColor = TileUtils.spriteApplyPaletteToIndex(spriteIdx, spritePalette);
                byte spriteBgPriority = spriteInfo[x].getBgPriority();

                if (spriteColor == 0) {    //If index 0, continue
                    continue;
                }

                //Priority 0 = Sprite on top of BG
                //Priority 1 = Only visible if background is color 0, or background is disabled
                if (spriteBgPriority == 0 || !gpuRegisters.bgWindowEnabled || bgColor == Lcd.COLOR_0_WHITE) {
                    mergedLine[x] = TileUtils.spriteApplyPaletteToIndex(spriteIdx, spritePalette);
                }
            }
        }
        return mergedLine;
    }


    public int[] getBgPalette() {
        return gpuRegisters.bgPalette;
    }

}
