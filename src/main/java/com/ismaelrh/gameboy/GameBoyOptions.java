package com.ismaelrh.gameboy;

public class GameBoyOptions {

    private long cycles = -1;

    private long speed = 1;


    public GameBoyOptions(long cycles, long speed) {
        this.cycles = cycles;
        this.speed = speed;
    }

    public long getCycles() {
        return cycles;
    }

    public void setCycles(long cycles) {
        this.cycles = cycles;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }
}
