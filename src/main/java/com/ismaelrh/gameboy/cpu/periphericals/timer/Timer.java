package com.ismaelrh.gameboy.cpu.periphericals.timer;

import com.ismaelrh.gameboy.cpu.memory.MMIODevice;

public class Timer extends MMIODevice {

    private final byte DIV_ADDRESS = (byte) 0xFF04;
    private final byte COUNTER_ADDRESS = (byte) 0xFF05;
    private final byte MODULO_ADDRESS = (byte) 0xFF06;
    private final byte CONTROL_ADDRESS = (byte) 0xFF07;


    private final DivTimer divTimer;
    private final CounterTimer counterTimer;

    public Timer() {
        super((char) 0xFF04, (char) 0xFF07);
        this.divTimer = new DivTimer();
        this.counterTimer = new CounterTimer();
    }

    public void tick(int cycles) {    //cycles = hertz
        divTimer.tick(cycles);
        counterTimer.tick(cycles);
    }

    @Override
    public void onWrite(char address, byte data) {
        //Div is cleared when written to
        if (address == DIV_ADDRESS) {
            divTimer.clear();
        }
        if (address == MODULO_ADDRESS) {
            counterTimer.setModulo(data);
        }
        if (address == CONTROL_ADDRESS) {
            counterTimer.setControl(data);
        }
    }

    @Override
    public byte onRead(char address) {
        if (address == DIV_ADDRESS) {
            return divTimer.getValue();
        }
        if (address == COUNTER_ADDRESS) {
            return counterTimer.getValue();
        }
        if (address == MODULO_ADDRESS) {
            return counterTimer.getModulo();
        }
        if (address == CONTROL_ADDRESS) {
            return counterTimer.getControl();
        }
        return 0;
    }


}
