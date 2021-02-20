package com.ismaelrh.gameboy.gpu;

import com.ismaelrh.gameboy.cpu.memory.Memory;

public class TileUtils {

    /**
     * A row of a tile is 2 bytes, in 2BPP format.
     * Returns an array of rgb colors to display, already transformed by the pallete.
     */
    public static int[] getRowOfTileColors(Memory memory, int[] pallete, char tileAddress, int rowNumber) {
        return applyPalleteToIndexes(getRowOfTileIndexes(memory, tileAddress, rowNumber), pallete);
    }

    /**
     * A row of a tile is 2 bytes, in 2BPP format.
     * Returns an array of int (0,1,2,3) for the given row of the given tile address.
     */
    public static int[] getRowOfTileIndexes(Memory memory, char tileAddress, int rowNumber) {
        char startAddress = (char) (tileAddress + rowNumber * 2);
        byte lowByte = memory.read(startAddress,true);
        byte highByte = memory.read((char) (startAddress + 1),true);

        int[] colorIndexes = new int[8];
        for (int i = 0; i < 8; i++) { //Compute each color
            byte mask = (byte) (1 << (7 - i)); //from left to right
            int c = 0;
            if ((lowByte & mask) != 0) c += 1;
            if ((highByte & mask) != 0) c += 2;
            colorIndexes[i] = c;
        }
        return colorIndexes;
    }


    public static int[] applyPalleteToIndexes(int[] colorIndexes, int[] pallete) {
        int[] result = new int[colorIndexes.length];
        for (int i = 0; i < colorIndexes.length; i++) {
            result[i] = pallete[colorIndexes[i]];
        }
        return result;
    }


}
