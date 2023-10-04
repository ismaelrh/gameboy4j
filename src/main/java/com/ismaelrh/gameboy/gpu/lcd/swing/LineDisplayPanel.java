package com.ismaelrh.gameboy.gpu.lcd.swing;

import com.ismaelrh.gameboy.gpu.lcd.Lcd;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import static com.ismaelrh.gameboy.gpu.lcd.Lcd.getColorCode;

public class LineDisplayPanel extends DisplayPanel  {

    private final int LCD_WIDTH;
    private final int LCD_HEIGHT;
    private final int SCALE;
    private final BufferedImage img;
    private final int[] rgb;
    private int pixel = 0;

    private boolean enabled = true;
    private boolean doRefresh = false;

    public LineDisplayPanel(int width, int height, int scale) {
        super();
        this.LCD_WIDTH = width;
        this.LCD_HEIGHT = height;
        this.SCALE = scale;
        rgb = new int[LCD_WIDTH * LCD_HEIGHT];
        img = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        //Initialize to white
        Arrays.fill(rgb, Lcd.BG_COLORS[0]);
    }

    @Override
    public void pushPixel(int color) {
        synchronized (this) {
            rgb[pixel] = color;
            pixel++;
            pixel = pixel % rgb.length;
        }

    }


    @Override
    protected void enableLcd() {
        enabled = true;
    }

    @Override
    protected void disableLcd() {
        enabled = false;
    }

    @Override
    public void onFrameFinished() {
        Arrays.fill(rgb, Lcd.BG_COLORS[0]);
        pixel = 0;
    }


    @Override
    public void onLineFinished() {
        //doRefresh = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        if (enabled) {
            g2d.drawImage(img, 0, 0, LCD_WIDTH * SCALE, LCD_HEIGHT * SCALE, null);
        } else {
            g2d.setColor(new Color(Lcd.BG_COLORS[0]));   //Put to white
            g2d.fillRect(0, 0, LCD_WIDTH * SCALE, LCD_HEIGHT * SCALE);
        }
        g2d.dispose();
    }

    private void flushFrame() {
        /*for(int i = 0; i < LCD_WIDTH; i++){
            rgb[LCD_WIDTH*95 + i] = Lcd.COLOR_2_DARK_GRAY;
        }
        for(int i = 0; i < LCD_WIDTH; i++){
            rgb[LCD_WIDTH*104 + i] = Lcd.COLOR_2_DARK_GRAY;
        }*/
        img.setRGB(0, 0, LCD_WIDTH, LCD_HEIGHT, rgb, 0, LCD_WIDTH);
        validate();
        repaint();
    }

    @Override
    public void run() {
        while (true) {
            //Wait until refresh, or any action, is asked for
            /*synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    break;
                }
            }*/

            //If refresh, then flush the frame

                flushFrame();



        }
    }

    public String getHash() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4 * rgb.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        for (int pixel : rgb) {
            buffer.putInt(getColorCode(pixel));
        }
        byte[] hash = MessageDigest.getInstance("MD5").digest(buffer.array());
        return Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public int getPixel() {
        return pixel;
    }
}