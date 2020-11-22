package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.*;
import static com.ismaelrh.gameboy.cpu.Registers.*;
import static org.junit.Assert.assertEquals;

public class LoadCommands16bTest {

    private LoadCommands16b loadCommands16b;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
        loadCommands16b = new LoadCommands16b(registers, memory);
        registers.setA((byte) 0xFF);
        registers.setB((byte) 0xFF);
        registers.setC((byte) 0xFF);
        registers.setD((byte) 0xFF);
        registers.setE((byte) 0xFF);
        registers.setH((byte) 0xFF);
        registers.setL((byte) 0xFF);
    }


    @Test
    public void loadRR_NN() {
        //nn <- 0xBEEF, BC <- 0xBEEF
        Instruction inst1 = new Instruction(getOpcode(0x00, BC, NONE), (char) 0xBEEF);
        short cycles = loadCommands16b.loadRR_NN(inst1);
        assertEquals16(0xBEEF, registers.getBC());
        assertEquals(12, cycles);

        //nn <- 0xDEAD, SP <- 0xDEAD
        Instruction inst2 = new Instruction(getOpcode(0x00, AF_SP, NONE), (char) 0xDEAD);

        loadCommands16b.loadRR_NN(inst2);
        assertEquals16(0xDEAD, registers.getSP());
    }

    @Test
    public void loadSP_HL() {
        //HL = 0xBEEF, SP <- 0xBEEF
        registers.setHL((char) 0xBEEF);
        short cycles = loadCommands16b.loadSP_HL();
        assertEquals16(0xBEEF, registers.getSP());
        assertEquals(8, cycles);
    }

    @Test
    public void push_QQ() {
        //SP = 0x0001, AF = 0xBEEF, (0x0000) <- 0xBE, (0xFFFF) <- 0xEF, SP <- 0xFFFF
        registers.setSP((char) 0x0001);
        registers.setAF((char) 0xBEEF);
        Instruction inst = new Instruction(getOpcode(0x3, AF_SP, NONE));
        short cycles = loadCommands16b.push_QQ(inst);

        assertEquals8(0xBE, memory.read((char) 0x0000));
        assertEquals8(0xEF, memory.read((char) 0xFFFF));
        assertEquals16(0xFFFF, registers.getSP());
        assertEquals(16, cycles);
    }

    @Test
    public void pop_QQ() {
        //SP = 0xFFFF, (0xFFFF) = 0xEF, (0x0000) = 0xBE, AF <- 0xBEEF, SP <- 0x0001
        registers.setSP((char) 0xFFFF);
        memory.write((char) 0xFFFF, (byte) 0xEF);
        memory.write((char) 0x0000, (byte) 0xBE);
        Instruction inst = new Instruction(getOpcode(0x3, AF_SP, NONE));
        short cycles = loadCommands16b.pop_QQ(inst);

        assertEquals16(0xBEEF, registers.getAF());
        assertEquals16(0x0001, registers.getSP());
        assertEquals(12, cycles);
    }
}
