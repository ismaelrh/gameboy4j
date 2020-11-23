package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.Memory;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertEquals8;
import static com.ismaelrh.gameboy.TestUtils.assertFlags;
import static com.ismaelrh.gameboy.cpu.Registers.E;
import static org.junit.Assert.*;

public class Arithmetic8BTest {

    private Arithmetic8b arithmetic8B;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
        arithmetic8B = new Arithmetic8b(registers, memory);
    }

    @Test
    public void addA_r_no_overflow() {
        registers.setA((byte) 0x5);
        registers.setE((byte) 0x3);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.addA_r(inst);

        assertFalse(registers.checkFlagZ());
        assertFalse(registers.checkFlagN());
        assertEquals8(0x8, registers.getA());    //Result is 8
        assertFlags(registers, false, false, false, false);
        assertEquals(4, cycles);
    }

    @Test
    public void addA_r_overflow() {
        registers.setA((byte) 255);
        registers.setE((byte) 2);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.addA_r(inst);
        assertEquals8(0x1, registers.getA());    //1
        assertFlags(registers, false, false, true, true);  //C-flag activated
        assertEquals(4, cycles);
    }

    @Test
    public void addA_r_half_overflow() {
        registers.setA((byte) 0xF);
        registers.setE((byte) 0x1);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.addA_r(inst);
        assertEquals8(0x10, registers.getA());    //1
        assertFlags(registers, false, false, true, false);  //H-flag activated
        assertEquals(4, cycles);
    }

    @Test
    public void addA_r_overflow_result_zero() {
        registers.setA((byte) 255);
        registers.setE((byte) 1);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.addA_r(inst);
        assertEquals8(0x0, registers.getA());    //1
        assertFlags(registers, true, false, true, true);  //C-flag activated
        assertEquals(4, cycles);
    }

    @Test
    public void addA_n_no_overflow() {
        registers.setA((byte) 0x5);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .withImmediate8b((byte) 0x3)
                .build();

        short cycles = arithmetic8B.addA_n(inst);

        assertFalse(registers.checkFlagZ());
        assertFalse(registers.checkFlagN());
        assertEquals8(0x8, registers.getA());    //Result is 8
        assertFlags(registers, false, false, false, false);
        assertEquals(8, cycles);
    }

    @Test
    public void addA_n_overflow() {
        registers.setA((byte) 255);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .withImmediate8b((byte) 0x2)
                .build();

        short cycles = arithmetic8B.addA_n(inst);
        assertEquals8(0x1, registers.getA());    //1
        assertFlags(registers, false, false, true, true);  //C-flag activated
        assertEquals(8, cycles);
    }

    @Test
    public void addA_n_half_overflow() {
        registers.setA((byte) 0xF);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .withImmediate8b((byte) 0x1)
                .build();

        short cycles = arithmetic8B.addA_n(inst);
        assertEquals8(0x10, registers.getA());    //1
        assertFlags(registers, false, false, true, false);  //H-flag activated
        assertEquals(8, cycles);
    }

    @Test
    public void addA_n_overflow_result_zero() {
        registers.setA((byte) 255);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .withImmediate8b((byte) 0x1)
                .build();

        short cycles = arithmetic8B.addA_n(inst);
        assertEquals8(0x0, registers.getA());    //1
        assertFlags(registers, true, false, true, true);  //C-flag activated
        assertEquals(8, cycles);
    }

    @Test
    public void addA_HL() {
        registers.setA((byte) 0xFF);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x0A);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = arithmetic8B.addA_HL(inst);

        assertEquals8(0x09, registers.getA());    //Result is 0xFF + 0x0A = 0x109 = 0x09 with overflow C and h
        assertFlags(registers, false, false, true, true);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_A_r_no_overflow() {

        registers.setA((byte) 0x01);
        registers.setE((byte) 0x02);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.addA_r(inst);

        assertEquals8(0x04, registers.getA());
        assertFlags(registers, false, false, false, false);
        assertEquals(4, cycles);
    }

    @Test
    public void addc_A_r_overflow() {

        registers.setA((byte) 0xFF);
        registers.setE((byte) 0x1);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.addA_r(inst);

        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, false, true, true);
        assertEquals(4, cycles);
    }

    @Test
    public void addc_A_r_overflow_zero() {

        registers.setA((byte) 0xFE);
        registers.setE((byte) 0x1);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.addA_r(inst);

        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, true);
        assertEquals(4, cycles);
    }

    @Test
    public void addc_A_r_half_overflow() {

        registers.setA((byte) 0xE1);
        registers.setE((byte) 0x0F);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.addA_r(inst);

        assertEquals8(0xF1, registers.getA());
        assertFlags(registers, false, false, true, false);
        assertEquals(4, cycles);
    }


    @Test
    public void addc_A_n_no_overflow() {

        registers.setA((byte) 0x01);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .withImmediate8b((byte) 0x02)
                .build();

        short cycles = arithmetic8B.addA_n(inst);

        assertEquals8(0x04, registers.getA());
        assertFlags(registers, false, false, false, false);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_A_n_overflow() {

        registers.setA((byte) 0xFF);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .withImmediate8b((byte) 0x1)
                .build();

        short cycles = arithmetic8B.addA_n(inst);

        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, false, true, true);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_A_n_overflow_zero() {

        registers.setA((byte) 0xFE);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x01)    //To indicate that operation is with carry
                .withImmediate8b((byte) 0x01)
                .build();

        short cycles = arithmetic8B.addA_n(inst);

        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, true);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_A_n_half_overflow() {

        registers.setA((byte) 0xE1);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .withImmediate8b((byte) 0x0F)
                .build();

        short cycles = arithmetic8B.addA_n(inst);

        assertEquals((byte) 0xF1, registers.getA());
        assertFlags(registers, false, false, true, false);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_A_HL_no_overflow() {

        registers.setA((byte) 0x01);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x02);

        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .build();

        short cycles = arithmetic8B.addA_HL(inst);

        assertEquals((byte) 0x04, registers.getA());
        assertFlags(registers, false, false, false, false);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_A_HL_overflow() {

        registers.setA((byte) 0xFF);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x01);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .build();

        short cycles = arithmetic8B.addA_HL(inst);

        assertEquals((byte) 0x01, registers.getA());
        assertFlags(registers, false, false, true, true);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_HL_n_overflow_zero() {

        registers.setA((byte) 0xFE);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x01);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x01)    //To indicate that operation is with carry
                .build();

        short cycles = arithmetic8B.addA_HL(inst);

        assertEquals((byte) 0x00, registers.getA());
        assertFlags(registers, true, false, true, true);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_A_HL_half_overflow() {

        registers.setA((byte) 0xE1);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x0F);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .build();

        short cycles = arithmetic8B.addA_HL(inst);

        assertEquals8(0xF1, registers.getA());
        assertFlags(registers, false, false, true, false);
        assertEquals(8, cycles);
    }

    @Test
    public void and_r_not_zero() {
        registers.setA((byte) 0x5A);
        registers.setE((byte) 0x3F);
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.and_r(inst);
        assertEquals(4, cycles);
        assertEquals8(0x1A, registers.getA());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void and_r_zero() {
        registers.setA((byte) 0xF0);
        registers.setE((byte) 0x0F);
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.and_r(inst);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, false);
    }

    @Test
    public void and_n_not_zero() {
        registers.setA((byte) 0x5A);
        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte)0x3F)
                .build();

        short cycles = arithmetic8B.and_n(inst);
        assertEquals(8, cycles);
        assertEquals8(0x1A, registers.getA());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void and_n_zero() {
        registers.setA((byte) 0xF0);
        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte)0x0F)
                .build();

        short cycles = arithmetic8B.and_n(inst);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, false);
    }

    @Test
    public void and_HL_not_zero() {
        registers.setA((byte) 0x5A);
        registers.setHL((char)0xBEBA);
        memory.write((char)0xBEBA,(byte)0x3F);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = arithmetic8B.and_HL(inst);
        assertEquals(8, cycles);
        assertEquals8(0x1A, registers.getA());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void and_HL_zero() {
        registers.setA((byte) 0xF0);
        registers.setHL((char)0xBEBA);
        memory.write((char)0xBEBA,(byte)0x0F);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = arithmetic8B.and_HL(inst);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, false);
    }

    @Test
    public void or_r_not_zero() {
        registers.setA((byte) 0xF0);
        registers.setE((byte) 0x0F);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.or_r(inst);
        assertEquals(4, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void or_r_zero() {
        registers.setA((byte) 0x00);
        registers.setE((byte) 0x00);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.or_r(inst);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void or_n_not_zero() {
        registers.setA((byte) 0xF0);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte)0x0F)
                .build();

        short cycles = arithmetic8B.or_n(inst);
        assertEquals(8, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void or_n_zero() {
        registers.setA((byte) 0x00);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte)0x00)
                .build();

        short cycles = arithmetic8B.or_n(inst);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void or_HL_not_zero() {
        registers.setA((byte) 0xF0);
        registers.setHL((char)0xBEBA);
        memory.write((char)0xBEBA,(byte)0x0F);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = arithmetic8B.or_HL(inst);
        assertEquals(8, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void or_HL_zero() {
        registers.setA((byte) 0x00);
        registers.setHL((char)0xBEBA);
        memory.write((char)0xBEBA,(byte)0x00);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.or_HL(inst);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }


    @Test
    public void xor_r_not_zero() {
        registers.setA((byte) 0x3F);
        registers.setE((byte) 0x3E);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.xor_r(inst);
        assertEquals(4, cycles);
        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void xor_r_zero() {
        registers.setA((byte) 0x3F);
        registers.setE((byte) 0x3F);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmetic8B.xor_r(inst);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }


    @Test
    public void xor_n_not_zero() {
        registers.setA((byte) 0x3F);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte)0x3E)
                .build();

        short cycles = arithmetic8B.xor_n(inst);
        assertEquals(8, cycles);
        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void xor_n_zero() {
        registers.setA((byte) 0x3F);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte)0x3F)
                .build();

        short cycles = arithmetic8B.xor_n(inst);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }


    @Test
    public void xor_HL_not_zero() {
        registers.setA((byte) 0x3F);
        registers.setHL((char)0xBEBA);
        memory.write((char)0xBEBA,(byte)0x3E);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = arithmetic8B.xor_HL(inst);
        assertEquals(8, cycles);
        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void xor_HL_zero() {
        registers.setA((byte) 0x3F);
        registers.setHL((char)0xBEBA);
        memory.write((char)0xBEBA,(byte)0x3F);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = arithmetic8B.xor_HL(inst);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }

}
