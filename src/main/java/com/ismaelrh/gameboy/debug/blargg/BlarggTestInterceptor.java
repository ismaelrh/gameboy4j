package com.ismaelrh.gameboy.debug.blargg;

import com.ismaelrh.gameboy.cpu.memory.MemoryInterceptor;

public class BlarggTestInterceptor extends MemoryInterceptor {


    @Override
    public byte onWrite(char address, byte data) {
        if (address == (char) 0xFF02 && data == (byte) 0x81) {
            char character = (char) (memory.read((char) 0xFF01) & 0xFF);
            System.out.print(character);
        }
        return data; //Do not modify data
    }

}
