package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.ControlUnit;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.cartridge.BasicCartridge;
import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import com.ismaelrh.gameboy.cpu.debug.console.BlarggTestInterceptor;
import com.ismaelrh.gameboy.cpu.debug.console.RegisterStatus;
import com.ismaelrh.gameboy.cpu.memory.Memory;

public class GameBoyDebugger {

    public static void main(String[] args) throws Exception {

        Memory memory = new Memory();
        Registers registers = new Registers();

        ControlUnit controlUnit = new ControlUnit(registers, memory);

        Cartridge cartridge = new BasicCartridge("Blargg CPU test 6", "/Users/ismaelrh/gb/06-ld r,r.gb");
        BlarggTestInterceptor blargg = new BlarggTestInterceptor();
        memory.addInterceptor(blargg);
        memory.insertCartridge(cartridge);

        RegisterStatus registerStatus = new RegisterStatus(registers);

        for (int i = 0; i < 1000; i++) {
            registerStatus.print();
            System.out.println();
            controlUnit.runInstruction();
        }
        blargg.flush();


    }
}
