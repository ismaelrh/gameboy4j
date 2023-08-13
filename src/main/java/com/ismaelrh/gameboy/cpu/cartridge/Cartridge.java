package com.ismaelrh.gameboy.cpu.cartridge;

//Cartridge: 0x0000 to 0x7FFF (32KB)
public interface Cartridge {


    byte read(char address);

    void write(char address, byte data);
}
