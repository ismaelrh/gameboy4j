package com.ismaelrh.gameboy;

public interface FrameFinishedListener extends GbListener {

    void frameFinished(GameBoy gameboy);
}
