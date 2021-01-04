package com.ismaelrh.gameboy.cpu.memory;

public abstract class MemoryInterceptor {

    protected Memory memory;

    public void addMemory(Memory memory) {
        this.memory = memory;
    }

    public byte onWrite(char address, byte data) {
        return data;
    }

    public byte onRead(char address, byte data) {
        return data;
    }
}
