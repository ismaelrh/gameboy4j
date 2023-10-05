package com.ismaelrh.gameboy.cpu.cartridge.rtc;

import com.ismaelrh.gameboy.cpu.Const;

public class RealTimeClock implements Clock {

    private ElapsedTime currentValue;
    private long valueTimestamp;

    private boolean isHalted = false;

    public RealTimeClock() {
        this.currentValue = new ElapsedTime();
        this.valueTimestamp = System.currentTimeMillis();
    }

    public RealTimeClock(ElapsedTime initialValue) {
        this.currentValue = initialValue;
        this.valueTimestamp = System.currentTimeMillis();
    }

    public void setHalt(boolean halt) {
        if (halt != isHalted) {
            isHalted = halt;
            if (!halt) {
                valueTimestamp = System.currentTimeMillis();
            }
        }
    }

    public int getSeconds() {
        return getValue().getSeconds();
    }

    public void setSeconds(int seconds) {
        getValue();
        currentValue.setSeconds(seconds);
    }

    public int getMinutes() {
        return getValue().getMinutes();
    }

    public void setMinutes(int minutes) {
        getValue();
        currentValue.setMinutes(minutes);
    }

    public int getHours() {
        return getValue().getHours();
    }

    public void setHours(int hours) {
        getValue();
        currentValue.setHours(hours);
        updateValue();
    }

    public int getDays() {
        return getValue().getDays();
    }

    public void setDays(int days) {
        updateValue();
        int daysToSet = days;
        if (currentValue.isDaysOverflow()) {
            daysToSet += 511;
        }
        currentValue.setDays(daysToSet);
    }

    @Override
    public boolean getDayOverflow() {
        return getValue().isDaysOverflow();
    }

    @Override
    public void setDayOverflow(boolean overflow) {
        getValue().setDaysOverflow(overflow);
    }

    public FixedClock getFixedClock() {
        return new FixedClock(getValue().copy());
    }


    public ElapsedTime getValue() {
        updateValue();
        return currentValue;
    }


    private void updateValue() {
        if (isHalted) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        int elapsedS = (int) ((currentTime - valueTimestamp) / 1000.0);
        if (elapsedS >= 1) {
            currentValue = currentValue.getPlusSeconds(elapsedS);
            valueTimestamp = currentTime;
        }
    }

    public boolean getHalted() {
        return isHalted;
    }

}
