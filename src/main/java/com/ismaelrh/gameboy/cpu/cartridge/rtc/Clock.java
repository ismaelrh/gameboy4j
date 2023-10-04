package com.ismaelrh.gameboy.cpu.cartridge.rtc;

public interface Clock {

    int getSeconds();

    void setSeconds(int seconds);

    int getMinutes();

    void setMinutes(int minutes);

    int getHours();

    void setHours(int hours);

    int getDays();

    void setDays(int days);

    boolean getDayOverflow();

    void setDayOverflow(boolean overflow);
}
