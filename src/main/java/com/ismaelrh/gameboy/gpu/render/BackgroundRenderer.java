package com.ismaelrh.gameboy.gpu.render;

import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.gpu.GpuRegisters;
import com.ismaelrh.gameboy.gpu.tiles.TileUtils;

import java.util.Arrays;

import static com.ismaelrh.gameboy.gpu.tiles.TileUtils.getTileAddress;

public class BackgroundRenderer {

    private final GpuRegisters gpuRegisters;
    private final Memory memory;

    public BackgroundRenderer(Memory memory, GpuRegisters gpuRegisters) {
        this.gpuRegisters = gpuRegisters;
        this.memory = memory;
    }

    public int[] getBackgroundIndexes(int drawingLine) {
        //Need to get the tile row and the pixel row we are going to draw,
        //according to scrollY and drawingLine
        int fullMapY = drawingLine + (gpuRegisters.scrollY & 0xFF);  //Line of the whole view to paint
        int verticalTilePos = fullMapY / 8; //Tile to draw
        int tileRow = fullMapY - verticalTilePos * 8;   //#row inside the tile (0..7)

        if (verticalTilePos >= 32) {
            verticalTilePos -= 32; //Wrap
        }

        //Need to read the line (# of tile, row of the tile, x scroll)
        return readBackgroundLine(verticalTilePos, tileRow, gpuRegisters.scrollX);
    }

    private int[] readBackgroundLine(int verticalTilePos, int tileRow, int scrollX) {

        //Need to read 160 pixels (5 tiles, but can be in the middle)
        int[] line = new int[160];

        if (!gpuRegisters.bgWindowEnabled) {
            Arrays.fill(line, -1);
            return line;
        }

        int horizontalTilePos = (scrollX & 0xFF) / 8;   //Number of tile (in row, horizontally)
        int xOffsetInsideFirstTile = (scrollX & 0xFF) - horizontalTilePos * 8;    //X offset inside tile
        int readPixels = 0;


        while (readPixels < 160) {

            char tileAddress = getTileAddress(memory,gpuRegisters,verticalTilePos, horizontalTilePos, false);
            int[] data = TileUtils.getRowOfTileIndexes(memory, tileAddress, tileRow, false, false);

            int startIdx = 0;
            int length = 8;

            //Cut the beginning of the screen
            if (readPixels == 0) {
                startIdx = xOffsetInsideFirstTile;
                length = 8 - xOffsetInsideFirstTile;
            }

            //Cut the end of the screen
            if (readPixels + 8 > 160) {
                length = 160 - readPixels;
            }

            System.arraycopy(data, startIdx, line, readPixels, length);
            readPixels += length;
            horizontalTilePos = (horizontalTilePos + 1) % 32;   //Wrap horizontal tile
        }
        return line;
    }


}
