package com.ismaelrh.gameboy.gpu.lcd.swing;

import javax.swing.*;

abstract class DisplayPanel extends JPanel implements Runnable {

    abstract public void pushPixel(int color);

    public void onFrameFinished(){}

    public void onLineFinished(){}

    abstract protected void enableLcd();

    abstract protected void disableLcd();

    abstract public String getHash() throws Exception;

    abstract public int getPixel();


}
