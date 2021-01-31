package com.ismaelrh.gameboy.cpu.memory;

public abstract class MMIODevice {

    protected Memory memory;
    protected char startAddress;
    protected char endAddress;

    public MMIODevice(char startAddress, char endAddress) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }

    public void addMemory(Memory memory) {
        this.memory = memory;
    }

    public abstract void onWrite(char address, byte data);

    public abstract byte onRead(char address);
}
