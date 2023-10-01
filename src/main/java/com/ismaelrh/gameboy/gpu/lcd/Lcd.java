package com.ismaelrh.gameboy.gpu.lcd;


public abstract class Lcd {

    public final static int COLOR_0_WHITE = rgbToInt(255, 255, 255, 255);
    public final static int COLOR_1_LIGHT_GRAY = rgbToInt(192, 192, 192, 255);
    public final static int COLOR_2_DARK_GRAY = rgbToInt(96, 96, 96, 255);
    public final static int COLOR_3_BLACK = rgbToInt(0, 0, 0, 255);
    public final static int COLOR_0_TRANSPARENT = rgbToInt(0, 0, 0, 0);


    public final static int[] BG_COLORS = new int[]{COLOR_0_WHITE, COLOR_1_LIGHT_GRAY, COLOR_2_DARK_GRAY, COLOR_3_BLACK};
    public final static int[] SPRITE_COLORS = new int[]{COLOR_0_TRANSPARENT, COLOR_1_LIGHT_GRAY, COLOR_2_DARK_GRAY, COLOR_3_BLACK};

    public abstract void pushPixel(int color); //0,1,2,3

    //Flush to actual screen
    public abstract void flush();

    public abstract void disableLcd();

    public abstract void enableLcd();


    /**
     * Returns a MD5 hash of the screen at this moment,
     * using the colors code, instead of the actual colors.
     */
    public abstract String getHash() throws Exception;

    public static int getColorCode(int color) {
        if (color == COLOR_0_WHITE) {
            return 0;
        } else if (color == COLOR_1_LIGHT_GRAY) {
            return 1;
        } else if (color == COLOR_2_DARK_GRAY) {
            return 2;
        } else if (color == COLOR_3_BLACK) {
            return 3;
        } else {
            return -1;
        }
    }

    public static int rgbToInt(int Red, int Green, int Blue, int Opacity) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.
        Opacity = (Opacity << 24) & 0xFF000000;
        return Opacity | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

}
