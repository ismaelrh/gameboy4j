package com.ismaelrh.gameboy.cpu.periphericals.timer;

import com.ismaelrh.gameboy.cpu.Const;
import com.ismaelrh.gameboy.cpu.memory.Memory;

public class CounterTimer {

    private final int[] FREQS = new int[]{4096, 262144, 65536, 16384};

    private byte value = 0;
    private byte modulo = 0; //Value to reset when overflow
    private byte control = 0;

    private boolean running = false;
    private int freq = 4096; //Initial is 4096Hz

    private int cyclesSinceReset = 0;

    private Memory memory;

    public CounterTimer(Memory memory) {
        this.memory = memory;
        this.cyclesSinceReset = getCycleRate(freq);
    }

    public void tick(int cycles) {
        if (running) {
            cyclesSinceReset += cycles;
            if (cyclesSinceReset >= getCycleRate(freq)) {
                inc();
                cyclesSinceReset = cyclesSinceReset - getCycleRate(freq); //To adjust
            }
        }
    }

    public byte getValue() {
        return (byte) (value & 0xFF);
    }

    private void inc() {
        value = (byte) ((value + 1) & 0xFF);
        if (value == 0) {
            value = modulo;
            memory.fireTimerInterruption();
        }
    }

    protected void setModulo(byte modulo) {
        this.modulo = (byte) (modulo & 0xFF);
    }

    public byte getModulo() {
        return modulo;
    }

    public byte getControl() {
        return control;
    }

    protected void setControl(byte control) {
        this.control = control;
        //Now, update internal state
        byte newFreq = (byte) (control & 0x03);
        setFreq(FREQS[newFreq]);
        byte newMode = (byte) (control & 0x04);
        if (newMode != 0) {
            start();
        } else {
            stop();
        }
    }

    private void setFreq(int hz) {
        this.freq = hz;
    }

    private void start() {
        if (!running) {
            cyclesSinceReset = freq;
            running = true;
        }

    }

    private void stop() {
        running = false;
    }


    private int getCycleRate(int freq) {
        return Const.CPU_FREQ_CYCLES_PER_S / freq;
    }


}
