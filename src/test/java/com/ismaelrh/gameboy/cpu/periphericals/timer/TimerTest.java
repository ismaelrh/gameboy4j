package com.ismaelrh.gameboy.cpu.periphericals.timer;

import com.ismaelrh.gameboy.cpu.memory.Memory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimerTest {

    private Timer2 timer;

    @Before
    public void setUp() {
        this.timer = new Timer2(new Memory());
    }

    @Test
    public void mode01_zeroIncrements() {
        timer.setControl((byte) 0x01);   //Freq to 16 cycles
        assertEquals(0,timer.getTimesToIncrement((char) 0x0000, (char) 0x000F));
    }

    @Test
    public void mode01_oneIncrementExact() {
        timer.setControl((byte) 0x01);   //Freq to 16 cycles
        assertEquals(1,timer.getTimesToIncrement((char) 0x0000, (char) 0x0010));
    }

    @Test
    public void mode01_oneIncrementNotExact() {
        timer.setControl((byte) 0x01);   //Freq to 16 cycles
        assertEquals(1,timer.getTimesToIncrement((char) 0x0000, (char) 0x0015));
    }


    @Test
    public void mode01_twoIncrements() {
        timer.setControl((byte) 0x01);   //Freq to 16 cycles
        //We start with 2 cycles remaining, and we do 20 cycles
        assertEquals(2,timer.getTimesToIncrement((char) 0x000E, (char)( 0x000E + 20)));
    }

    @Test
    public void mode01_threeIncrements() {
        timer.setControl((byte) 0x01);   //Freq to 16 cycles
        //We start with 2 cycles remaining, and we do 20 cycles
        assertEquals(3,timer.getTimesToIncrement((char) 0x000E, (char)( 0x000E + 36)));
    }

    @Test
    public void mode01_overflowProducesIncrement() {
        timer.setControl((byte) 0x01);   //Freq to 16 cycles
        //We start with 1 cycles remaining, and we do 5 cycles
        assertEquals(1,timer.getTimesToIncrement((char) 0xFFFF, (char)( 0x0004)));
    }


    @Test
    public void mode01_overflowProducesIncrementOfTwo() {
        timer.setControl((byte) 0x01);   //Freq to 16 cycles
        //We start with 1 cycles remaining, and we do 20 cycles
        assertEquals(2,timer.getTimesToIncrement((char) 0xFFFF, (char)( 0xFFFF + 17)));
    }


}

