package com.ismaelrh.gameboy.cpu.debug.console;

import com.ismaelrh.gameboy.cpu.memory.MemoryInterceptor;

public class BlarggTestInterceptor extends MemoryInterceptor {

    private String buffer = "";

    @Override
    public byte onWrite(char address, byte data) {
        if (address == (char) 0xFF02 && data == (byte) 0x81) {
            char character = (char) (memory.read((char) 0xFF01) & 0xFF);
            buffer += character;
        }
        return data; //Do not modify data
    }

    public void flush() {
        System.out.println("Blargg: " + buffer);
    }
}
