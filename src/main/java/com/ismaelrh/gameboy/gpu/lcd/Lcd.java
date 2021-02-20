package com.ismaelrh.gameboy.gpu.lcd;


public abstract class Lcd {

    public final static int COLOR_0 = rgbToInt(255, 255, 255); //Color.BLACK.getRGB();
    public final static int COLOR_1 = rgbToInt(192, 192, 192);
    public final static int COLOR_2 = rgbToInt(96, 96, 96);
    public final static int COLOR_3 = rgbToInt(0, 0, 0);

    public abstract void putPixel(int color); //0,1,2,3

    //Flush to actual screen
    public abstract void flush();

    public abstract void disableLcd();

    public abstract void enableLcd();

    public static int rgbToInt(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
