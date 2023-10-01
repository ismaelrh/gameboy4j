package com.ismaelrh.gameboy.debug.tileset;


import com.ismaelrh.gameboy.FrameFinishedListener;
import com.ismaelrh.gameboy.GameBoy;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.gpu.Gpu;
import com.ismaelrh.gameboy.gpu.tiles.TileUtils;
import com.ismaelrh.gameboy.gpu.lcd.swing.DisplayPanel;

import javax.swing.*;
import java.awt.*;

public class TileSetDisplay implements FrameFinishedListener {

    private final static int LCD_WIDTH = 128;
    private final static int LCD_HEIGHT = 128;
    private final DisplayPanel panel;
    private final char startAddress;

    public TileSetDisplay(char startAddress) {
        this.panel = new DisplayPanel(LCD_WIDTH, LCD_HEIGHT, 1);
        this.panel.setPreferredSize(new Dimension(LCD_WIDTH, LCD_HEIGHT));
        new Thread(this.panel).start();
        this.startAddress = startAddress;
    }

    public JPanel getDisplayPanel() {
        return panel;
    }

    @Override
    public void frameFinished(GameBoy gameboy) {
        display(gameboy.getMemory(), gameboy.getGpu());
    }

    private void display(Memory memory, Gpu gpu) {
        //Each tileset has 256 tiles of 8x8 bytes. I can display all of them in a 128x128 screen (16x16 tiles)
        //Display everything
        for (int line = 0; line < 128; line++) {
            int tileY = line / 8;   //no of vertical tile
            int tileRow = line - tileY * 8; //row inside the specific tile row
            for (int tileX = 0; tileX < 16; tileX++) {  //each tile (there are 16)
                int totalTileNumber = tileY * 16 + tileX;
                int tileStartOffset = totalTileNumber * 16;
                char tileAddress = (char) (startAddress + tileStartOffset);
                int[] pixels = TileUtils.getRowOfTileColors(memory, gpu.getBgPalette(), tileAddress, tileRow);
                for (int pixel : pixels) {
                    panel.pushPixel(pixel);
                }
            }
        }

        panel.requestRefresh();
    }


}
