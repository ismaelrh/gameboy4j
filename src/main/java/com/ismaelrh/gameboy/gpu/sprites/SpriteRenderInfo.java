package com.ismaelrh.gameboy.gpu.sprites;

public class SpriteRenderInfo {

    private int index;

    private byte bgPriority;

    private byte paletteIdx;

    public SpriteRenderInfo(int index, byte bgPriority, byte palette) {
        this.index = index;
        this.bgPriority = bgPriority;
        this.paletteIdx = palette;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public byte getBgPriority() {
        return bgPriority;
    }

    public void setBgPriority(byte bgPriority) {
        this.bgPriority = bgPriority;
    }

    public byte getPaletteIdx() {
        return paletteIdx;
    }

    public void setPaletteIdx(byte paletteIdx) {
        this.paletteIdx = paletteIdx;
    }
}
