package com.ismaelrh.gameboy.cpu.gpu.lcd;

import java.awt.image.BufferedImage;

/**
 * Abstraction, independent of the actual implementation used to display.
 */
public class CanvasLcd implements Lcd {

    private final static int LCD_WIDTH = 160;
    private final static int LCD_HEIGHT = 144;

    private final BufferedImage buffer;

    public CanvasLcd() {
        this.buffer = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        System.out.println("Initiating Canvas LCD...");
    }

    public void drawLine(int linenumber, byte[] bytes) {

    }

    //Flush to actual screen
    public void flush() {

    }

}
