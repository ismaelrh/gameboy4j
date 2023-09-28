package com.ismaelrh.gameboy.gpu;

/**
 * Encapsulates the state of the GPU.
 */
public class GpuRegisters {

    //Basic Registers
    public int mode = 2;
    public int currentClock = 0;
    public int line = 0;
    public boolean lcdEnabled = true;
    public int gpuIRQ = 0;

    //BG and Window
    public int tileAddressingMode = 0; //0 -> Uses 8800 + signed method. 1 -> Uses 8000 + unsigned method.
    public int bgTileMap = 0; //0 or 1
    public final int[] bgPalette = new int[4];
    public byte bg_palette_reg = (byte) 0x00;
    public boolean bgWindowEnabled = false;
    public int windowTileMap = 0;
    public boolean windowEnabled = false;  //0xFF40, bit 5
    public byte wY = 0x00;    //0xFF4A
    public byte wX = 0x00;    //0xFF4B
    public byte windowY = 0x00;  //Internal window line counter


    //Sprites
    public boolean spritesEnabled = false;
    public int spritesSizeMode = 0;
    public final int[] spritePalette0 = new int[4];
    public byte sprite_palette_0_reg = (byte) 0x00;
    public final int[] spritePalette1 = new int[4];
    public byte sprite_palette_1_reg = (byte) 0x00;
    public final int[][] spritePalletes = new int[][]{spritePalette0, spritePalette1};


    // LCD
    public byte lcd_control = (byte) 0x91;
    public byte lcd_stat = (byte) 0x87; //8th bit is always set at start
    public byte scrollY = (byte) 0x00;
    public byte scrollX = (byte) 0x00;
    public byte lcd_ly = (byte) 0x00;
    public byte lcd_lyc = (byte) 0x00;

}
