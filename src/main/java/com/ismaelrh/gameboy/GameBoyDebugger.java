package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.ControlUnit;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.cartridge.BasicCartridge;
import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import com.ismaelrh.gameboy.debug.blargg.BlarggTestInterceptor;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.debug.debugger.console.ConsoleController;
import com.ismaelrh.gameboy.debug.debugger.DebuggerController;
import com.ismaelrh.gameboy.debug.logCheck.LogStatusProvider;
import com.ismaelrh.gameboy.debug.logCheck.binjgb.BinJgbLogStatusProvider;

public class GameBoyDebugger {

    public static void main(String[] args) throws Exception {

        Memory memory = new Memory();
        Registers registers = new Registers();
        registers.initForRealGB();

        ControlUnit controlUnit = new ControlUnit(registers, memory);

        //Register console debugger
        controlUnit.setDebuggerController(new ConsoleController());

        //Log status provider
        //controlUnit.setLogStatusProvider(new BinJgbLogStatusProvider("/Users/ismaelrh/gb/binjgb/bin/test.txt"));

        //Register blargg interceptor to get output and put it on console
        memory.addInterceptor(new BlarggTestInterceptor());

        Cartridge cartridge = new BasicCartridge("Blargg CPU test 6", "/Users/ismaelrh/gb/01-special.gb");
        memory.insertCartridge(cartridge);


        while (true) {
            controlUnit.runInstruction();
        }
        //blargg.flush();


    }
}
