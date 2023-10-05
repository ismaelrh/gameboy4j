package com.ismaelrh.gameboy.cpu.periphericals.timer;

import com.ismaelrh.gameboy.cpu.memory.MMIODevice;
import com.ismaelrh.gameboy.cpu.memory.Memory;

public class Timer2 extends MMIODevice {

    private static final int[] CYCLES_FREQ = {1024, 16, 64, 256};
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

    public Timer2(Memory memory) {
        super((char) 0xFF04, (char) 0xFF07);
        this.memory = memory;
    }

    public void tick(int cycles) {
        updateInternalCounter((char) (internalCounter & 0xFFFF), (char) ((internalCounter + cycles) & 0xFFFF));
        if (overflow) {
            //ticksSinceOverflow += cycles;
            //overflow = false;
           // memory.fireTimerInterruption();
            /*if (ticksSinceOverflow >= 4 && ticksSinceOverflow < 6) {
                memory.fireTimerInterruption();
                interruptionDone = true;
            }
            if (ticksSinceOverflow >= 6) {
                if (!interruptionDone) {
                    memory.fireTimerInterruption();
                } else {
                    interruptionDone = false;
                }
                timaCounter = modulo;
                overflow = false;
                ticksSinceOverflow = 0;
            }*/
        }
    }

    private void incrementTimer() {
        timaCounter = (byte) ((timaCounter + 1) & 0xFF);
        if (timaCounter == 0x00) {  //Overflow
            overflow = true;
            ticksSinceOverflow = 0;
            memory.fireTimerInterruption();
        }
    }

    private void setModulo(byte data) {
        modulo = (byte) (data & 0xFF);
    }


    protected void setControl(byte data) {
        control = (byte) (data & 0xFF);
    }


    @Override
    public void onWrite(char address, byte data) {
        //Div is cleared when written to
        if (address == DIV_ADDRESS) {
            updateInternalCounter(((char) (internalCounter & 0xFFFF)), (char) 0);
            //TODO: No ponemos al valor inicial? Diria que NO
        } else if (address == COUNTER_ADDRESS) {
            timaCounter = data;
            /*if (ticksSinceOverflow < 5) {
                timaCounter = data;
                overflow = false;
                ticksSinceOverflow = 0;
            }*/ //TODO ???

        } else if (address == MODULO_ADDRESS) {
            setModulo(data);
        } else if (address == CONTROL_ADDRESS) {
            setControl(data);
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


    private void updateInternalCounter(char oldValue, char newValue) {
        this.internalCounter = newValue;

        int times = (control & (1 << 2)) != 0  ? getTimesToIncrement(oldValue,newValue) : 0;
        for(int i = 0; i < times;i++){
            incrementTimer();
        }


    }

    protected int getTimesToIncrement(char oldValue, char newValue) {
        this.internalCounter = newValue;
        int times = 0;

        //Debo saber cuantas veces ha pasado por el múltiplo, sin contar el viejo
        int cyclesToIncrement = CYCLES_FREQ[control & 0b11];

        int prevRemainingToLatch = cyclesToIncrement - oldValue % cyclesToIncrement;  //Cuantos ciclos le faltaban para triggear
        int increasedCycles = (newValue - oldValue);

        if(Integer.compareUnsigned(newValue,oldValue)< 0 && newValue!=0x00){
             increasedCycles = newValue + prevRemainingToLatch;
        }

        if (increasedCycles >= prevRemainingToLatch) {
            times += 1;
            increasedCycles -= prevRemainingToLatch;
        }

        times += (increasedCycles / cyclesToIncrement);
        return times;
    }


    //int bitPos = FREQUENCE_TO_BITS[control & 0b11];
    //boolean bit = (internalCounter & (1 << bitPos)) != 0;
    //bit &= (control & (1 << 2)) != 0;


      /*private void updateInternalCounter(char oldValue, char newValue) {
        this.internalCounter = newValue;
        int bitPos = FREQUENCE_TO_BITS[control & 0b11];
        boolean bit = (internalCounter & (1 << bitPos)) != 0;
        bit &= (control & (1 << 2)) != 0;
        if (!bit && previousBit) {
            incrementTimer();
        }
        previousBit = bit;
    }*/

}
