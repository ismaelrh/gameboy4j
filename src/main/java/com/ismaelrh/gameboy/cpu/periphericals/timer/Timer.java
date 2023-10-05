package com.ismaelrh.gameboy.cpu.periphericals.timer;

import com.ismaelrh.gameboy.cpu.memory.MMIODevice;
import com.ismaelrh.gameboy.cpu.memory.Memory;

/**
 * Got from https://github.com/trekawek/coffee-gb/blob/master/src/main/java/eu/rekawek/coffeegb/timer/Timer.java
 * as I was not able to make it work completely.
 */
public class Timer extends MMIODevice {

    private static final int[] FREQ_TO_BIT = {9, 3, 5, 7};

    private int div, tac, tma, tima;

    private boolean previousBit;

    private boolean overflow;

    private int ticksSinceOverflow;

    protected Memory memory;

    public Timer(Memory memory) {
        super((char) 0xFF04, (char) 0xFF07);
        this.memory = memory;
    }

    public void tick() {
        updateDiv((div + 1) & 0xffff);
        if (overflow) {
            ticksSinceOverflow++;
            if (ticksSinceOverflow == 4) {
                memory.fireTimerInterruption();
            }
            if (ticksSinceOverflow == 5) {
                tima = tma;
            }
            if (ticksSinceOverflow == 6) {
                tima = tma;
                overflow = false;
                ticksSinceOverflow = 0;
            }
        }
    }

    private void incTima() {
        tima++;
        tima %= 0x100;
        if (tima == 0) {
            overflow = true;
            ticksSinceOverflow = 0;
        }
    }

    private void updateDiv(int newDiv) {
        this.div = newDiv;
        int bitPos = FREQ_TO_BIT[tac & 0b11];
        //bitPos <<= speedMode.getSpeedMode() - 1;
        boolean bit = (div & (1 << bitPos)) != 0;
        bit &= (tac & (1 << 2)) != 0;
        if (!bit && previousBit) {
            incTima();
        }
        previousBit = bit;
    }

    public void onWrite(char address, byte value) {
        switch (address) {
            case 0xff04:
                updateDiv(0);
                break;

            case 0xff05:
                if (ticksSinceOverflow < 5) {
                    tima = value;
                    overflow = false;
                    ticksSinceOverflow = 0;
                }
                break;

            case 0xff06:
                tma = value;
                break;

            case 0xff07:
                tac = value;
                break;
        }
    }

    @Override
    public byte onRead(char address) {
        switch (address) {
            case 0xff04:
                return (byte) ((div >> 8) & 0xFF);

            case 0xff05:
                return (byte) (tima & 0xFF);

            case 0xff06:
                return (byte) (tma & 0xFF);

            case 0xff07:
                return (byte) ((tac | 0b11111000) & 0xFF);
        }
        throw new IllegalArgumentException();
    }
}