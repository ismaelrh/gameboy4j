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

    public SwingLcd(int scale) {
        this.panel = new DisplayPanel(LCD_WIDTH, LCD_HEIGHT, scale);
        this.panel.setPreferredSize(new Dimension(LCD_WIDTH * scale, LCD_HEIGHT * scale));
        new Thread(this.panel).start();
    }

    @Override
    public void pushPixel(int rgbColor) {
        this.panel.pushPixel(rgbColor);
    }

    @Override
    public void flush() {
        this.panel.requestRefresh();
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
