package com.ismaelrh.gameboy.debug;

import com.ismaelrh.gameboy.FrameFinishedListener;
import com.ismaelrh.gameboy.GameBoy;

import javax.swing.*;

public class FpsInfo implements FrameFinishedListener {

    private int currentFrames = 0;
    private long totalCycles = 0L;
    private long startTimeMs = 0L;

    private final JFrame frame;

    public FpsInfo(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void frameFinished(GameBoy gameboy) {
        long currentTimeMs = System.currentTimeMillis();
        currentFrames += 1;

        if (startTimeMs == 0L) {
            startTimeMs = currentTimeMs;
        }
        if (currentTimeMs - startTimeMs < 1000) {
            totalCycles = gameboy.getTotalCycles();
        } else {
            updateGui(currentTimeMs);
            currentFrames = 0;
            startTimeMs = currentTimeMs;
        }
    }


    private void updateGui(long currentTimeMs) {
        this.frame.setTitle("FPS: " + getFps(currentTimeMs) + " - Cycles: " + (int)(totalCycles/1_000)+ "K");
    }

    private long getFps(long currentTimeMs) {
        return Math.round(currentFrames / ((currentTimeMs - startTimeMs) / 1000.0));
    }




}
