package com.ismaelrh.gameboy.gpu.lcd.swing;

import com.ismaelrh.gameboy.gpu.lcd.Lcd;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DisplayPanel extends JPanel implements Runnable {

    private final int LCD_WIDTH;
    private final int LCD_HEIGHT;
    private final int SCALE;

    private final BufferedImage img;
    private final int[] rgb;
    private int[] frozenRgb;
    private int pixel = 0;
    private boolean enabled = true;
    private boolean doRefresh = false;

    public DisplayPanel(int width, int height, int scale) {
        super();
        this.LCD_WIDTH = width;
        this.LCD_HEIGHT = height;
        this.SCALE = scale;
        rgb = new int[LCD_WIDTH * LCD_HEIGHT];
        frozenRgb = new int[LCD_WIDTH * LCD_HEIGHT];
        img = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        //Initialize to white
        Arrays.fill(rgb, Lcd.RGB_COLORS[0]);
    }

    public void pushPixel(int color) {
        synchronized(this){
            rgb[pixel] = color;
            pixel++;
            pixel = pixel % rgb.length;
        }

    }

    protected void enableLcd() {
        enabled = true;
    }

    protected void disableLcd() {
        enabled = false;
    }

    public void requestRefresh() {
        synchronized (this) {
            doRefresh = true;
            pixel = 0;
            frozenRgb = Arrays.copyOf(rgb, rgb.length);
            notifyAll();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        if (enabled) {
            g2d.drawImage(img, 0, 0, LCD_WIDTH * SCALE, LCD_HEIGHT * SCALE, null);
        } else {
            g2d.setColor(new Color(Lcd.RGB_COLORS[0]));   //Put to white
            g2d.fillRect(0, 0, LCD_WIDTH * SCALE, LCD_HEIGHT * SCALE);
        }
        g2d.dispose();
    }

    private void flushFrame() {
        img.setRGB(0, 0, LCD_WIDTH, LCD_HEIGHT, frozenRgb, 0, LCD_WIDTH);
        validate();
        repaint();
    }

    @Override
    public void run() {
        while (true) {
            //Wait until refresh, or any action, is asked for
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    break;
                }
            }

            //If refresh, then flush the frame
            if (doRefresh) {
                flushFrame();
                synchronized (this) {
                    doRefresh = false;
                }
            }

        }
    }
}