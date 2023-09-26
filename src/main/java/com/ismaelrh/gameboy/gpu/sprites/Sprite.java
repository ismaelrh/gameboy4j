package com.ismaelrh.gameboy.gpu.sprites;

public class Sprite {

    private int posY = 0;

    private int posX = 0;

    private byte tileNumber = 0;

    private byte palette = 0;

    private byte xFlip = 0;

    private byte yFlip = 0;

    private byte priority = 0;

    protected void refresh(byte[] data) { //It receives 4 bytes
        posY = Byte.toUnsignedInt(data[0]);
        posX = Byte.toUnsignedInt(data[1]);
        tileNumber = data[2];
        refreshAttributes(data[3]);
    }

    private void refreshAttributes(byte attributes) {
        palette = (byte) ((attributes >> 4) & 0x01);    //byte-4
        xFlip = (byte) ((attributes >> 5) & 0x01);    //byte-5
        yFlip = (byte) ((attributes >> 6) & 0x01);    //byte-6
        priority = (byte) ((attributes >> 7) & 0x01);    //byte-7
    }

    public int getPosY(){
        return  (posY - 16);
    }

    public int getPosX(){
        return  (posX - 8);
    }

    public int getRawPosY() {
        return posY;
    }

    public int getRawPosX() {
        return posX;
    }

    public byte getTileNumber() {
        return tileNumber;
    }

    public byte getPalette() {
        return palette;
    }

    public byte getxFlip() {
        return xFlip;
    }

    public byte getyFlip() {
        return yFlip;
    }

    public byte getPriority() {
        return priority;
    }
}
