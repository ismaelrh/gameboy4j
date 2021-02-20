package com.ismaelrh.gameboy.debug.tileset;


import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.gpu.Gpu;
import com.ismaelrh.gameboy.gpu.TileUtils;
import com.ismaelrh.gameboy.gpu.lcd.swing.DisplayPanel;

import javax.swing.*;
import java.awt.*;

public class TileSetDisplay {

    private final static int LCD_WIDTH = 128;
    private final static int LCD_HEIGHT = 128;
    private final DisplayPanel panel;
    private final char startAddress;
    private final Memory memory;
    private final Gpu gpu;

    int[] currentScreen = new int[128*128];

    public TileSetDisplay(Memory memory, Gpu gpu, char startAddress) {
        this.panel = new DisplayPanel(LCD_WIDTH, LCD_HEIGHT, 1);
        this.panel.setPreferredSize(new Dimension(LCD_WIDTH * 1, LCD_HEIGHT * 1));
        new Thread(this.panel).start();
        this.startAddress = startAddress;
        this.memory = memory;
        this.gpu = gpu;
    }

    public JPanel getDisplayPanel() {
        return panel;
    }

    public void display() {
        //Display everything
        for (int line = 0; line < 128; line++) {
            int tileY = line / 8;
            int tileRow = line - tileY * 8;
            for (int tileX = 0; tileX < 16; tileX++) {
                int totalTileNumber = tileY * 16 + tileX;
                int tileStartOffset = totalTileNumber * 16;
                char tileAddress = (char) (startAddress + tileStartOffset);
                int[] pixels = TileUtils.getRowOfTileColors(memory, gpu.getPallete(), tileAddress, tileRow);
                for (int pixel : pixels) {
                    panel.pushPixel(pixel);
                }
            }
        }

        panel.requestRefresh();
    }
}
