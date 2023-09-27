package com.ismaelrh.gameboy.gpu;

import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.gpu.lcd.Lcd;

public class TileUtils {

    /**
     * A row of a tile is 2 bytes, in 2BPP format.
     * Returns an array of rgb colors to display, already transformed by the palette.
     */
    public static int[] getRowOfTileColors(Memory memory, int[] palette, char tileAddress, int rowNumber) {
        return applyPaletteToIndexes(getRowOfTileIndexes(memory, tileAddress, rowNumber, false, false), palette);
    }

    /**
     * A row of a tile is 2 bytes, in 2BPP format.
     * Returns an array of int (0,1,2,3) for the given row of the given tile address.
     */
    public static int[] getRowOfTileIndexes(Memory memory, char tileAddress, int rowNumber, boolean hFlip, boolean vFlip) {
        return getRowOfTileIndexes(memory, tileAddress, rowNumber, hFlip, vFlip, 8);
    }
    public static int[] getRowOfTileIndexes(Memory memory, char tileAddress, int rowNumber, boolean hFlip, boolean vFlip, int tileHeight) {

        if(vFlip){
            rowNumber = Math.abs((tileHeight-1)-rowNumber);
        }

        char startAddress = (char) (tileAddress + rowNumber * 2);
        byte lowByte = memory.read(startAddress,true);
        byte highByte = memory.read((char) (startAddress + 1),true);

        int[] colorIndexes = new int[8];
        for (int i = 0; i < 8; i++) { //Compute each color
            int effectiveIndex = i;
            if(hFlip){
                effectiveIndex = Math.abs(7 - i);
            }
            byte mask = (byte) (1 << (7 - effectiveIndex)); //from left to right
            int c = 0;
            if ((lowByte & mask) != 0) c += 1;
            if ((highByte & mask) != 0) c += 2;
            colorIndexes[i] = c;
        }
        return colorIndexes;
    }


    public static int[] applyPaletteToIndexes(int[] colorIndexes, int[] palette) {
        int[] result = new int[colorIndexes.length];
        for (int i = 0; i < colorIndexes.length; i++) {
            result[i] = palette[colorIndexes[i]];
        }
        return result;
    }

    public static int bgApplyPaletteToIndex(int colorIndex, int[] palette) {
        if(colorIndex==-1){ //No bg defined, we use black.
            return Lcd.COLOR_0_WHITE;
        }
        return palette[colorIndex];
    }

    public static int spriteApplyPaletteToIndex(int colorIndex, int[] palette) {
        if(colorIndex==-1){
            return Lcd.COLOR_2_DARK_GRAY;
        }
        return palette[colorIndex];
    }


}
