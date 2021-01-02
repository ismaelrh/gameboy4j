package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.instructions.SingleBit;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertEquals8;
import static com.ismaelrh.gameboy.TestUtils.assertFlags;
import static org.junit.Assert.assertEquals;

public class SingleBitTest {

    private SingleBit singleBit;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
        singleBit = new SingleBit(registers, memory);
    }


    @Test
    public void bit_n_r_0() {
        //3rd bit is 0, so will be set
        registers.setE((byte) 0xAA); //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 2) //3rd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.bit_n_r(inst);
        assertEquals(8, cycles);
        assertEquals8(0xAA, registers.getE());
        assertFlags(registers, true, false, true, true);
    }

    @Test
    public void bit_n_hl_0() {
        //3rd bit is 0, so will be set
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xAA);     //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 2) //3rd lsb
                .build();

        short cycles = singleBit.bit_n_HL(inst);
        assertEquals(12, cycles);
        assertEquals8(0xAA, memory.read((char) 0xBEBA));  //Register untouched
        assertFlags(registers, true, false, true, true);
    }

    @Test
    public void bit_n_r_1() {
        //4rd bit is 1, so will be reset
        registers.setE((byte) 0xAA); //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 3) //4rd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.bit_n_r(inst);
        assertEquals(8, cycles);
        assertEquals8(0xAA, registers.getE());
        assertFlags(registers, false, false, true, true);
    }

    @Test
    public void bit_n_hl_1() {
        //3rd bit is 0, so will be set
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xAA);     //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 3) //4rd lsb
                .build();

        short cycles = singleBit.bit_n_HL(inst);
        assertEquals(12, cycles);
        assertEquals8(0xAA, memory.read((char) 0xBEBA));  //Register untouched
        assertFlags(registers, false, false, true, true);
    }

    @Test
    public void set_n_r_0() {
        registers.setE((byte) 0xAA); //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 2) //2nd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.set_n_r(inst);
        assertEquals(8, cycles);
        assertEquals8(0xAE, registers.getE());
        assertFlags(registers, true, true, true, true); //Untouched
    }

    @Test
    public void set_n_HL_0() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xAA);     //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 2) //2nd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.set_n_HL(inst);
        assertEquals(16, cycles);
        assertEquals8(0xAE, memory.read((char)0xBEBA));
        assertFlags(registers, true, true, true, true); //Untouched
    }

    @Test
    public void set_n_1() {
        registers.setE((byte) 0xAA); //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 3) //2nd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.set_n_r(inst);
        assertEquals(8, cycles);
        assertEquals8(0xAA, registers.getE());
        assertFlags(registers, true, true, true, true); //Untouched
    }


    @Test
    public void set_n_HL_1() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xAA);     //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 3) //2nd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.set_n_HL(inst);
        assertEquals(16, cycles);
        assertEquals8(0xAA, memory.read((char)0xBEBA));
        assertFlags(registers, true, true, true, true); //Untouched
    }

    @Test
    public void res_n_r_0() {
        registers.setE((byte) 0xAA); //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 2) //2nd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.res_n_r(inst);
        assertEquals(8, cycles);
        assertEquals8(0xAA, registers.getE());
        assertFlags(registers, true, true, true, true); //Untouched
    }

    @Test
    public void res_n_HL_0() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xAA);     //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 2) //2nd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.res_n_HL(inst);
        assertEquals(16, cycles);
        assertEquals8(0xAA, memory.read((char)0xBEBA));
        assertFlags(registers, true, true, true, true); //Untouched
    }

    @Test
    public void res_n_r_1() {
        registers.setE((byte) 0xAA); //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 1) //2nd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.res_n_r(inst);
        assertEquals(8, cycles);
        assertEquals8(0xA8, registers.getE());
        assertFlags(registers, true, true, true, true); //Untouched
    }


    @Test
    public void res_n_HL_1() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xAA);     //1010 1010
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 1) //2nd lsb
                .withSecondOperand(Registers.E)
                .build();

        short cycles = singleBit.res_n_HL(inst);
        assertEquals(16, cycles);
        assertEquals8(0xA8, memory.read((char)0xBEBA));
        assertFlags(registers, true, true, true, true); //Untouched
    }

}

