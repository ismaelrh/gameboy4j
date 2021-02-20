package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.Const;
import com.ismaelrh.gameboy.cpu.ControlUnit;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.cartridge.BasicCartridge;
import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import com.ismaelrh.gameboy.gpu.Gpu;
import com.ismaelrh.gameboy.gpu.lcd.swing.SwingLcd;
import com.ismaelrh.gameboy.gpu.lcd.Lcd;
import com.ismaelrh.gameboy.cpu.periphericals.timer.Timer;
import com.ismaelrh.gameboy.debug.blargg.BlarggTestInterceptor;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.debug.debugger.console.ConsoleController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class GameBoyDebugger {

    private static final Logger log = LogManager.getLogger(GameBoyDebugger.class);


    public static void main(String[] args) throws Exception {

        Memory memory = new Memory();
        Registers registers = new Registers();
        registers.initForRealGB();
        Timer timer = new Timer(memory);

        SwingLcd lcd = new SwingLcd(2);
        Gpu gpu = new Gpu(memory, lcd);
        startGUI(lcd.getDisplayPanel());

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


            int cycles = controlUnit.runInstruction();
            controlUnit.checkInterruptions();
            timer.tick(cycles);
            gpu.tick(cycles);

            remainingCyclesPerFrame -= cycles;

            if (remainingCyclesPerFrame <= 0) {
                lcd.flush();    //Flush the LCD
                long nanosEndFrame = System.nanoTime();
                long elapsedTimeNanos = (nanosEndFrame - nanosStartFrame);
                long remainingTimeNanos = Const.NANOS_PER_FRAME - elapsedTimeNanos;

                if (remainingTimeNanos > 0) {
                    long millisToSleep = remainingTimeNanos / 1000000;
                    int nanosToSleep = (int) (remainingTimeNanos - millisToSleep * 1000000);
                    Thread.sleep(millisToSleep, nanosToSleep);
                }
                nanosStartFrame = System.nanoTime();
                remainingCyclesPerFrame = Const.CYCLES_PER_FRAME;

            }


            totalC += cycles;
            if (totalC % 1000000 == 0) {
                double totalTime = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.println("Pace: " + totalC / (totalTime * Const.CYCLES_PER_FRAME) + " frames/s");
            }


        }
        //blargg.flush();
    }

    private static JFrame startGUI(JPanel display) {
        JFrame window = new JFrame("gameboy4j");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setContentPane(display);
        window.setResizable(false);
        window.setVisible(true);
        window.pack();
        return window;
    }
}
