package com.ismaelrh.gameboy.cpu.periphericals.timer;

import com.ismaelrh.gameboy.cpu.Const;

public class DivTimer {

    private final int FREQ = 16384; //16384hz

    private int cyclesSinceReset = getCycleRate();
    private byte value = 0;

    protected void tick(int cycles) {
        cyclesSinceReset += cycles;
        if (cyclesSinceReset >= getCycleRate()) {
            inc();
            cyclesSinceReset = cyclesSinceReset - getCycleRate(); //To adjust
        }
    }

    protected byte getValue() {
        return (byte) (value & 0xFF);
    }

    protected void clear() {
        this.value = 0;
    }

    private void inc() {
        value = (byte) ((value + 1) & 0xFF);
    }

    private int getCycleRate() {
        return Const.CPU_FREQ_CYCLES_PER_S / FREQ;
    }

}
