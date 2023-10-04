package com.ismaelrh.gameboy.cpu.cartridge.rtc;

public class FixedClock implements Clock {

    private final ElapsedTime time;

    public FixedClock(ElapsedTime time) {
        this.time = time;
    }

    public int getSeconds() {
        return time.getSeconds();
    }

    public void setSeconds(int seconds) {

    }

    public int getMinutes() {
        return time.getMinutes();
    }

    public void setMinutes(int minutes) {

    }

    public int getHours() {
        return time.getHours();
    }

    public void setHours(int hours) {

    }

    public int getDays() {
        return time.getDays();
    }

    public void setDays(int days) {

    }

    @Override
    public boolean getDayOverflow() {
        return time.isDaysOverflow();
    }

    @Override
    public void setDayOverflow(boolean overflow) {

    }

    public ElapsedTime getTime() {
        return time;
    }
}
