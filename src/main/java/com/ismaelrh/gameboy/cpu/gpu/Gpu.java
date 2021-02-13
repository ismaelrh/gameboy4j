package com.ismaelrh.gameboy.cpu.gpu;

import com.ismaelrh.gameboy.cpu.gpu.lcd.Lcd;
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

    private int gpuIRQ = 0;

    private final int OAM_MODE = 2;
    private final int VRAM_MODE = 3;
    private final int HBLANK_MODE = 0;
    private final int VBLANK_MODE = 1;

    private final int OAM_CYCLES = 80;
    private final int VRAM_CYCLES = 172;
    private final int HBLANK_CYCLES = 204;
    private final int VBLANK_CYCLES = 456;

    private final char LCD_STAT_ADDRESS = (char) 0xFF41;
    private byte lcd_stat = (byte) 0x80; //8th bit is always set

    private final char LCD_LY_ADDRESS = (char) 0xFF44;
    private byte lcd_ly = (byte) 0x00;

    private final char LCD_LYC_ADDRESS = (char) 0xFF45;
    private byte lcd_lyc = (byte) 0x00;

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
            default:
                //Any other is not written
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
            //End of scanline, write it to framebuffer
        }
    }

    private void hblankMode() {
        if (currentClock <= HBLANK_CYCLES) {
            currentClock -= HBLANK_CYCLES;

            setLine(line + 1);

            if (line == 143) {  //enter vblank if 143 lines
                setPpuMode(VBLANK_MODE);
                //TODO: Display when entering this mode
            } else { //If not 143 lines, return to OAM mode
                setPpuMode(OAM_MODE);
            }
        }

    }

    private void vblankMode() {
        //Fire Blank interruption, a new frame was drawn
        memory.fireVBlankInterruption();
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
    }

    /**
     * Done when:
     * starting up LCD
     * changing LYC
     * changing line
     */
    private void checkForStatIRQ() {
        if (gpuIRQ == 0 && isTriggerConditionMeet()) {
            gpuIRQ = 1;
            memory.fireLcdInterruption();
        }
        if (!isTriggerConditionMeet()) {
            gpuIRQ = 0;
        }
    }

    private boolean isTriggerConditionMeet() {
        //There is an LCD interruption condition met
        return (mode == HBLANK_MODE && (byte) (lcd_stat & 0x08) != 0) ||
                (mode == VBLANK_MODE && (byte) (lcd_stat & 0x10) != 0) ||
                (mode == OAM_MODE && (byte) (lcd_stat & 0x20) != 0) ||
                (lcd_ly == lcd_lyc && (byte) (lcd_stat & 0x40) != 0);
    }


}
