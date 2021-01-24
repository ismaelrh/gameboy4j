package com.ismaelrh.gameboy.cpu;

public class ExecutionInfo {

    private int cycles = 0;

    public ExecutionInfo() {
    }

    public int getCycles() {
        return cycles;
    }

    public void addCycles(int cycles) {
        this.cycles += cycles;
    }


}
