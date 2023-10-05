package com.ismaelrh.gameboy.cpu.periphericals.timer;

import com.ismaelrh.gameboy.cpu.memory.MMIODevice;
import com.ismaelrh.gameboy.cpu.memory.Memory;

public class Timer extends MMIODevice {

    private static final int[] FREQUENCE_TO_BITS = {9, 3, 5, 7};
    private static final char DIV_ADDRESS = (char) 0xFF04;
    private static final char COUNTER_ADDRESS = (char) 0xFF05;
    private static final char MODULO_ADDRESS = (char) 0xFF06;
    private static final char CONTROL_ADDRESS = (char) 0xFF07;


    //Counter Timer (Tima) values
    private byte timaCounter = 0;
    private byte modulo = 0; //Value to reset when overflow
    private byte control = 0;
    private boolean overflow;

    private int ticksSinceOverflow;

    //16bit internal counter, incremented every cycle
    private char internalCounter = 0x00;
    private boolean previousBit = false;
    private boolean interruptionDone = false;

    public Timer(Memory memory) {
        super((char) 0xFF04, (char) 0xFF07);
        this.memory = memory;
    }

    public void tick(int cycles) {
        updateInternalCounter((char) ((internalCounter + cycles) & 0xFFFF));
        if (overflow) {
            ticksSinceOverflow += cycles;
            if (ticksSinceOverflow >= 4 && ticksSinceOverflow < 6) {
                memory.fireTimerInterruption();
                interruptionDone = true;
            }
            if(ticksSinceOverflow>=6){
                if(!interruptionDone){
                    memory.fireTimerInterruption();
                }
                else{
                    interruptionDone = false;
                }
                timaCounter = modulo;
                overflow = false;
                ticksSinceOverflow = 0;
            }
        }
    }

    private void incrementTimer() {
        timaCounter = (byte) ((timaCounter + 1) & 0xFF);
        if (timaCounter == 0x00) {  //Overflow
            overflow = true;
            ticksSinceOverflow = 0;
        }
    }

    private void setModulo(byte data) {
        modulo = (byte) (data & 0xFF);
    }


    @Override
    public void onWrite(char address, byte data) {
        //Div is cleared when written to
        if (address == DIV_ADDRESS) {
            updateInternalCounter((char) 0);
            //TODO: No ponemos al valor inicial? Diria que NO
        } else if (address == COUNTER_ADDRESS) {
            if (ticksSinceOverflow < 5) {
                timaCounter = data;
                overflow = false;
                ticksSinceOverflow = 0;
            }

        } else if (address == MODULO_ADDRESS) {
            setModulo(data);
        } else if (address == CONTROL_ADDRESS) {
            control = data;
        }
    }

    @Override
    public byte onRead(char address) {
        if (address == DIV_ADDRESS) {
            return getDivValue();
        }
        if (address == COUNTER_ADDRESS) {
            return timaCounter;
        }
        if (address == MODULO_ADDRESS) {
            return modulo;
        }
        if (address == CONTROL_ADDRESS) {
            return (byte) (control | 0xF8);
        }
        return 0;
    }


    //Div value = higher 8bits of internal counter
    private byte getDivValue() {
        return (byte) ((internalCounter >> 8) & 0xFF);
    }


    private void updateInternalCounter(char newValue) {
        this.internalCounter = newValue;
        int bitPos = FREQUENCE_TO_BITS[control & 0b11];
        boolean bit = (internalCounter & (1 << bitPos)) != 0;
        bit &= (control & (1 << 2)) != 0;
        if (!bit && previousBit) {
            incrementTimer();
        }
        previousBit = bit;
    }

}
