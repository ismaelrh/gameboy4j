package com.ismaelrh.gameboy.gpu.lcd.swing;

import com.ismaelrh.gameboy.gpu.lcd.Lcd;

import javax.swing.*;
import java.awt.*;

/**
 * Abstraction, independent of the actual implementation used to display.
 */
public class SwingLcd extends Lcd {

    private final static int LCD_WIDTH = 160;
    private final static int LCD_HEIGHT = 144;
    private final DisplayPanel panel;

    public int getPixel() {
        return panel.getPixel();
    }

    public SwingLcd(int scale) {
        this.panel = new FrameDisplayPanel(LCD_WIDTH, LCD_HEIGHT, scale);
        this.panel.setPreferredSize(new Dimension(LCD_WIDTH * scale, LCD_HEIGHT * scale));
        new Thread(this.panel).start();
    }

    @Override
    public void pushPixel(int rgbColor) {
        this.panel.pushPixel(rgbColor);
    }

    @Override
    public void frameFinished() {
        this.panel.onFrameFinished();
    }

    @Override
    public void lineFinished() {
        this.panel.onLineFinished();
    }

    @Override
    public void disableLcd() {
        this.panel.disableLcd();
    }

    @Override
    public void enableLcd() {
        this.panel.enableLcd();
    }

    @Override
    public String getHash() throws Exception {
        return this.panel.getHash();
    }

    public JPanel getDisplayPanel() {
        return panel;
    }


}
