package com.ismaelrh.gameboy.cpu;

public class Const {

    public static int CPU_FREQ_CYCLES_PER_S = 41943040; //Cycles per second
    public static double SECONDS_PER_CYCLE = 1.0 / CPU_FREQ_CYCLES_PER_S;
    public static double MILLIS_PER_CYCLE = SECONDS_PER_CYCLE / 1000.0;

    public static double DISPLAY_FRAMES_PER_S = 60.0;
    public static double CYCLES_PER_FRAME = CPU_FREQ_CYCLES_PER_S / DISPLAY_FRAMES_PER_S;
    public static double MILLIS_PER_FRAME = (1/DISPLAY_FRAMES_PER_S)*1_000.0; //16.67 MILLIS_PER_CYCLE * CYCLES_PER_FRAME;
    public static long NANOS_PER_FRAME = (long) (MILLIS_PER_FRAME * 1_000_000.0);

}
