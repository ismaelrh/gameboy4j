package com.ismaelrh.gameboy.cpu.instructions.implementation;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.cartridge.FakeCartridge;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.*;
import static com.ismaelrh.gameboy.cpu.Registers.*;
import static org.junit.Assert.assertEquals;

public class Load16BTest {

    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        registers.initForTest();
        memory = new Memory();
        memory.insertCartridge(new FakeCartridge());
        registers.setA((byte) 0xFF);
        registers.setB((byte) 0xFF);
        registers.setC((byte) 0xFF);
        registers.setD((byte) 0xFF);
        registers.setE((byte) 0xFF);
        registers.setH((byte) 0xFF);
        registers.setL((byte) 0xFF);
    }

    @Test
    public void loadnn_SP() {
        registers.setSP((char) 0xBEBA);

        //(nn) <- SP-l
        //(nn+1) <- SP-h

        Instruction inst = new InstructionBuilder().withImmediate16b((char)0xCAFE).build();
        short cycles = Load16b.loadnn_SP(inst, memory, registers);
        assertEquals(20,cycles);
        assertEquals8(0xBA,memory.read((char)0xCAFE));
        assertEquals8(0xBE,memory.read((char)0xCAFF));
    }

    @Test
    public void loadRR_NN() {
        //nn <- 0xBEEF, BC <- 0xBEEF
        Instruction inst1 = new Instruction(getOpcodeDoubleRegister(0x00, BC, NONE), (char) 0xBEEF);
        short cycles = Load16b.loadRR_NN(inst1, memory, registers);
        assertEquals16(0xBEEF, registers.getBC());
        assertEquals(12, cycles);

        //nn <- 0xDEAD, SP <- 0xDEAD
        Instruction inst2 = new Instruction(getOpcodeDoubleRegister(0x00, AF_SP, NONE), (char) 0xDEAD);

        Load16b.loadRR_NN(inst2, memory, registers);
        assertEquals16(0xDEAD, registers.getSP());
    }

    @Test
    public void loadSP_HL() {
        //HL = 0xBEEF, SP <- 0xBEEF
        registers.setHL((char) 0xBEEF);
        short cycles = Load16b.loadSP_HL(new Instruction((byte) 0x0), memory, registers);
        assertEquals16(0xBEEF, registers.getSP());
        assertEquals(8, cycles);
    }

    @Test
    public void push_QQ() {
        //SP = 0x0001, AF = 0xBEEF, (0x0000) <- 0xBE, (0xFFFF) <- 0xEF, SP <- 0xFFFF
        registers.setSP((char) 0x0001);
        registers.setAF((char) 0xBEEF);
        Instruction inst = new Instruction(getOpcodeDoubleRegister(0x3, AF_SP, NONE));
        short cycles = Load16b.push_QQ(inst, memory, registers);

        assertEquals8(0xBE, memory.read((char) 0x0000));
        assertEquals8(0xE0, memory.read((char) 0xFFFF));
        assertEquals16(0xFFFF, registers.getSP());
        assertEquals(16, cycles);
    }

    @Test
    public void pop_QQ() {
        //SP = 0xFFFF, (0xFFFF) = 0xEF, (0x0000) = 0xBE, AF <- 0xBEEF, SP <- 0x0001
        registers.setSP((char) 0xFFFF);
        memory.write((char) 0xFFFF, (byte) 0xEF);
        memory.write((char) 0x0000, (byte) 0xBE);
        Instruction inst = new Instruction(getOpcodeDoubleRegister(0x3, AF_SP, NONE));
        short cycles = Load16b.pop_QQ(inst, memory, registers);

        //Last nibble of F should never be modified
        assertEquals16(0xBEE0, registers.getAF());
        assertEquals16(0x0001, registers.getSP());
        assertEquals(12, cycles);
    }
}
