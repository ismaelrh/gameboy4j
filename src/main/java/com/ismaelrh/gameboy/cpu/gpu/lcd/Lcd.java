package com.ismaelrh.gameboy.cpu.gpu.lcd;

public interface Lcd {

    void drawLine(int linenumber, byte[] bytes);

    //Flush to actual screen
    void flush();
}
