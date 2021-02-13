package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.Const;
import com.ismaelrh.gameboy.cpu.ControlUnit;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.cartridge.BasicCartridge;
import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import com.ismaelrh.gameboy.cpu.gpu.Gpu;
import com.ismaelrh.gameboy.cpu.gpu.lcd.CanvasLcd;
import com.ismaelrh.gameboy.cpu.gpu.lcd.Lcd;
import com.ismaelrh.gameboy.cpu.periphericals.timer.Timer;
import com.ismaelrh.gameboy.debug.blargg.BlarggTestInterceptor;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.debug.debugger.console.ConsoleController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class GameBoyDebugger {

    private static final Logger log = LogManager.getLogger(GameBoyDebugger.class);


    public static void main(String[] args) throws Exception {

        Memory memory = new Memory();
        Registers registers = new Registers();
        registers.initForRealGB();

        Timer timer = new Timer(memory);
        Lcd lcd = new CanvasLcd();
        Gpu gpu = new Gpu(memory, lcd);

        ControlUnit controlUnit = new ControlUnit(registers, memory);

        //Register console debugger
        controlUnit.setDebuggerController(new ConsoleController());

        //Log status provider
        //controlUnit.setLogStatusProvider(new BinJgbLogStatusProvider("/Users/ismaelrh/gb/binjgb/bin/test.txt"));

        //Register blargg interceptor to get output and put it on console
        memory.addInterceptor(new BlarggTestInterceptor());
        memory.addMMIODevice(timer);

        Cartridge cartridge = new BasicCartridge("Blargg CPU test 6", "/Users/ismaelrh/gb/01-special.gb");
        memory.insertCartridge(cartridge);

        double remainingCyclesPerFrame = Const.CYCLES_PER_FRAME;
        long nanosStartFrame = System.nanoTime();

        //I end on the end of every frame to check
        long totalC = 0;
        long startTime = System.currentTimeMillis();
        while (true) {

            nanosStartFrame = System.nanoTime();

            int cycles = controlUnit.runInstruction();
            controlUnit.checkInterruptions();
            timer.tick(cycles);
            gpu.tick(cycles);

            remainingCyclesPerFrame -= cycles;
            if (remainingCyclesPerFrame <= 0) {
                remainingCyclesPerFrame = Const.CYCLES_PER_FRAME;
            }

            long nanosEndFrame = System.nanoTime();
            long elapsedTimeNanos = (nanosEndFrame - nanosStartFrame);
            long remainingTimeNanos = Const.NANOS_PER_FRAME - elapsedTimeNanos;

            /*totalC += cycles;
            if(totalC%1000000==0){
                double totalTime = (System.currentTimeMillis() - startTime)/1000.0;
                System.out.println("Pace: " + totalC/(totalTime*Const.CYCLES_PER_FRAME) + " frames/s");
            }*/

            if (remainingTimeNanos <= 0) {
                //log.warn("Emulator cannot keep pace");
            } else {
                long millisToSleep = remainingTimeNanos / 1000000;
                int nanosToSleep = (int) (remainingTimeNanos - millisToSleep * 1000000);
                Thread.sleep(millisToSleep, nanosToSleep);
            }

        }
        //blargg.flush();


    }
}
