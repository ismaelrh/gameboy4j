package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;

public class ExecutionInfo {

    private int cycles = 0;
    private Instruction currentInstruction;

    public ExecutionInfo() {
    }

    public int getCycles() {
        return cycles;
    }

    public void addCycles(int cycles) {
        this.cycles += cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public Instruction getCurrentInstruction() {
        return currentInstruction;
    }

    public void setCurrentInstruction(Instruction currentInstruction) {
        this.currentInstruction = currentInstruction;
    }

    public String getCurrentInstMnemonic() {
        return this.currentInstruction.getDescription().getMnemonic();
    }
}
