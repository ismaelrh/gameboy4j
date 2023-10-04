package com.ismaelrh.gameboy.gpu;

import com.ismaelrh.gameboy.gpu.lcd.Lcd;

/**
 * Handles writes and reads from memory bus into GPU registers.
 */
public class GpuRegistersMapping{

    private final Gpu gpu;
    private final GpuRegisters gpuRegisters;

    public GpuRegistersMapping(Gpu gpu,GpuRegisters gpuRegisters) {
        this.gpu = gpu;
        this.gpuRegisters = gpuRegisters;
    }

    //Internal registers mapping
    public static final char LCD_WY_ADDRESS = (char) 0xFF4A;
    public static final char LCD_WX_ADDRESS = (char) 0xFF4B;
    public static final char LCD_CONTROL_ADDRESS = (char) 0xFF40;
    public static final char LCD_STAT_ADDRESS = (char) 0xFF41;
    public static final char LCD_SCROLL_Y_ADDRESS = (char) 0xFF42;
    public static final char LCD_SCROLL_X_ADDRESS = (char) 0xFF43;
    public static final char LCD_LY_ADDRESS = (char) 0xFF44;
    public static final char LCD_LYC_ADDRESS = (char) 0xFF45;
    public static final char LCD_BG_PALETTE_ADDRESS = (char) 0xFF47;
    public static final char LCD_SPRITE_PALETTE_0_ADDRESS = (char) 0xFF48;
    public static final char LCD_SPRITE_PALETTE_1_ADDRESS = (char) 0xFF49;

    public byte onRead(char address) {
        switch (address) {
            case LCD_STAT_ADDRESS:  //Most significant bit is always 1
                return (byte) (gpuRegisters.lcd_stat | 0x80);
            case LCD_LY_ADDRESS:
                return gpuRegisters.lcd_ly;
            case LCD_LYC_ADDRESS:
                return gpuRegisters.lcd_lyc;
            case LCD_CONTROL_ADDRESS:
                return gpuRegisters.lcd_control;
            case LCD_BG_PALETTE_ADDRESS:
                return gpuRegisters.bg_palette_reg;
            case LCD_SPRITE_PALETTE_0_ADDRESS:
                return gpuRegisters.sprite_palette_0_reg;
            case LCD_SPRITE_PALETTE_1_ADDRESS:
                return gpuRegisters.sprite_palette_1_reg;
            case LCD_SCROLL_Y_ADDRESS:
                return gpuRegisters.scrollY;
            case LCD_SCROLL_X_ADDRESS:
                return gpuRegisters.scrollX;
            case LCD_WY_ADDRESS:
                return gpuRegisters.wY;
            case LCD_WX_ADDRESS:
                return gpuRegisters.wX;
            default:
                return (byte) 0xFF;
        }
    }

    public void onWrite(char address, byte data) {
        switch (address) {
            case LCD_STAT_ADDRESS:
                //Only can write bits 3 to 6
                byte dataToWrite = (byte) (data & 0x78); //Keep only bits 3-6 from input
                //Remove bits 3-6 from data and write the new data
                gpuRegisters.lcd_stat = (byte) (((gpuRegisters.lcd_stat & 0x87) | dataToWrite) & 0xFF);
                break;
            case LCD_LYC_ADDRESS:
                gpuRegisters.lcd_lyc = data;
                gpu.checkForStatIRQ();
                break;
            case LCD_CONTROL_ADDRESS:
                gpuRegisters.lcd_control = data;
                checkLcdEnabled();
                checkBgWindowEnabled();
                checkWindowEnabled();
                checkSpritesEnabled();
                checkSpritesSizeMode();
                updateBgTileMap();
                updateWindowTileMap();
                updateTileAddressingMode();
                break;
            case LCD_BG_PALETTE_ADDRESS:
                gpuRegisters.bg_palette_reg = data;
                updateBgPalettes();
                break;
            case LCD_SPRITE_PALETTE_0_ADDRESS:
                gpuRegisters.sprite_palette_0_reg = data;
                updateSpritesPalettes();
                break;
            case LCD_SPRITE_PALETTE_1_ADDRESS:
                gpuRegisters.sprite_palette_1_reg = data;
                updateSpritesPalettes();
                break;
            case LCD_SCROLL_Y_ADDRESS:
                gpuRegisters.scrollY = data;
                break;
            case LCD_SCROLL_X_ADDRESS:
                gpuRegisters.scrollX = data;
                break;
            case LCD_WY_ADDRESS:
                gpuRegisters.wY = data;
                break;
            case LCD_WX_ADDRESS:
                gpuRegisters.wX = data;
                break;
            default:
                //Any other is not written
        }
    }

    private String f(byte data) {
        return String.format("%04X", data);
    }

    private void updateBgPalettes() {

        for (int i = 0; i < 4; i++) {
            int value = ((gpuRegisters.bg_palette_reg & (3 << 2 * i)) >> 2 * i) & 0xFF;
            gpuRegisters.bgPalette[i] = Lcd.BG_COLORS[value];
        }

    }

    private void updateSpritesPalettes() {

        //0 is always transparent
        gpuRegisters.spritePalette0[0] = Lcd.COLOR_0_TRANSPARENT;
        gpuRegisters.spritePalette1[0] = Lcd.COLOR_0_TRANSPARENT;

        for (int i = 1; i < 4; i++) {
            int value = ((gpuRegisters.sprite_palette_0_reg & (3 << 2 * i)) >> 2 * i) & 0xFF;
            gpuRegisters.spritePalette0[i] = Lcd.SPRITE_COLORS[value];
        }
        for (int i = 1; i < 4; i++) {
            int value = ((gpuRegisters.sprite_palette_1_reg & (3 << 2 * i)) >> 2 * i) & 0xFF;
            gpuRegisters.spritePalette1[i] = Lcd.SPRITE_COLORS[value];
        }
    }

    private void checkLcdEnabled() {
        if ((gpuRegisters.lcd_control & 0x80) != 0) {
            gpu.enableLcd();
        } else {
            gpu.disableLcd();
        }
    }

    private void checkBgWindowEnabled() {
        gpuRegisters.bgWindowEnabled = (gpuRegisters.lcd_control & 0x01) != 0;
    }

    private void checkWindowEnabled() {
        gpuRegisters.windowEnabled = (gpuRegisters.lcd_control & 0x20) != 0;
    }

    private void checkSpritesEnabled() {
        gpuRegisters.spritesEnabled = (gpuRegisters.lcd_control & 0x02) != 0;
    }

    private void checkSpritesSizeMode() {
        gpuRegisters.spritesSizeMode = (gpuRegisters.lcd_control & 0x04) >> 2;
    }

    //bit 3
    private void updateBgTileMap() {
        if ((gpuRegisters.lcd_control & 0x08) != 0) {
            gpuRegisters.bgTileMap = 1;
        } else {
            gpuRegisters.bgTileMap = 0;
        }
    }

    private void updateWindowTileMap() {
        if ((gpuRegisters.lcd_control & 0x40) != 0) {
            gpuRegisters.windowTileMap = 1;
        } else {
            gpuRegisters.windowTileMap = 0;
        }
    }

    //bit 4
    private void updateTileAddressingMode() {
        if ((gpuRegisters.lcd_control & 0x10) != 0) {
            gpuRegisters.tileAddressingMode = 1;
        } else {
            gpuRegisters.tileAddressingMode = 0;    //tileSet = 0, tileMap = 1
        }
    }


}
