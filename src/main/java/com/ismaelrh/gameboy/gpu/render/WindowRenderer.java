package com.ismaelrh.gameboy.gpu.render;

import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.gpu.GpuRegisters;
import com.ismaelrh.gameboy.gpu.tiles.TileUtils;

import java.util.Arrays;

import static com.ismaelrh.gameboy.gpu.tiles.TileUtils.getTileAddress;

public class WindowRenderer {

    private final GpuRegisters gpuRegisters;
    private final Memory memory;

    public WindowRenderer(Memory memory, GpuRegisters gpuRegisters) {
        this.gpuRegisters = gpuRegisters;
        this.memory = memory;
    }

    public int[] getWindowIndexes(int drawingLine, boolean windowOn) {

        boolean windowReached = drawingLine - (gpuRegisters.wY & 0xFF) >= 0;
        int tileMapRow = (gpuRegisters.windowY & 0xFF); //Row of tilemap to draw.
        int verticalTilePos = tileMapRow / 8; //Tile to draw. Never will be >= 32.
        int tileRow = tileMapRow - verticalTilePos * 8;   //#row inside the tile (0..7)

        //if WY > 144 or WX > 166 or Y < 0, we do not draw
        if (!windowOn || Byte.compareUnsigned(gpuRegisters.wY, (byte) 144) > 0 || Byte.compareUnsigned(gpuRegisters.wX, (byte) 166) > 0 || !windowReached) {
            int[] whiteLine = new int[160];
            Arrays.fill(whiteLine, -1);
            return whiteLine;
        }

        return readWindowLine(verticalTilePos, tileRow, gpuRegisters.wX);
    }

    private int[] readWindowLine(int verticalTilePos, int tileRow, int windowX) {

        int windowRealX = windowX - 7;

        int[] line = new int[160];
        Arrays.fill(line, -1);

        if (!gpuRegisters.bgWindowEnabled || !gpuRegisters.windowEnabled) {
            return line;    //All empty
        }


        int currentScreenPixel = Math.max(windowRealX, 0); //if < 0, we start drawing at 0 in screen.
        int currentTileLinePixel = 0;

        //In this scanline, there is window to draw, so we increase.
        if (currentScreenPixel < 160) {
            gpuRegisters.windowY += 1;
        }

        while (currentScreenPixel < 160) {
            int remainingPixels = 160 - currentScreenPixel;
            int dataLength = Math.min(remainingPixels, 8);
            int horizontalTilePos = currentTileLinePixel / 8;
            char tileAddress = getTileAddress(memory, gpuRegisters, verticalTilePos, horizontalTilePos, true);
            int[] data = TileUtils.getRowOfTileIndexes(memory, tileAddress, tileRow, false, false);
            System.arraycopy(data, 0, line, currentScreenPixel, dataLength);
            currentScreenPixel += dataLength;
            currentTileLinePixel += dataLength;
        }

        return line;
    }
}
