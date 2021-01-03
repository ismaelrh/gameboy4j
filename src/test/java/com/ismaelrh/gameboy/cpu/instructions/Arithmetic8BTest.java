package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.instructions.implementation.Arithmetic8b;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertEquals8;
import static com.ismaelrh.gameboy.TestUtils.assertFlags;
import static com.ismaelrh.gameboy.cpu.Registers.E;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Arithmetic8BTest {

    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
    }

    @Test
    public void cp_r_no_borrow() {
        registers.setA((byte) 0xBA);
        registers.setE((byte) 0x01);

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x7)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.cp_r(inst, memory, registers);

        assertEquals8(0xBA, registers.getA());    //Result is not modified
        assertFlags(registers, false, true, false, false);
        assertEquals(4, cycles);
    }

    @Test
    public void cp_r_zero() {
        registers.setA((byte) 0xBA);
        registers.setE((byte) 0xBA);

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x7)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.cp_r(inst, memory, registers);

        assertEquals8(0xBA, registers.getA());
        assertFlags(registers, true, true, false, false);
        assertEquals(4, cycles);
    }

    @Test
    public void cp_r_borrow() {
        registers.setA((byte) 0x3E);     //62
        registers.setE((byte) 0X40);     //64

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x7)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.cp_r(inst, memory, registers);

        assertEquals8(0x3E, registers.getA());
        assertFlags(registers, false, true, false, true);
        assertEquals(4, cycles);
    }

    @Test
    public void cp_r_half_borrow() {
        registers.setA((byte) 0x3E);     //0
        registers.setE((byte) 0X0F);     //255

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x7)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x3E, registers.getA());
        assertFlags(registers, false, true, true, false);
        assertEquals(4, cycles);
    }


    @Test
    public void subA_r_no_borrow() {
        registers.setA((byte) 0xBA);
        registers.setE((byte) 0x01);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0xB9, registers.getA());    //Result is 8
        assertFlags(registers, false, true, false, false);
        assertEquals(4, cycles);
    }


    @Test
    public void subA_r_zero() {
        registers.setA((byte) 0xBA);
        registers.setE((byte) 0xBA);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x0, registers.getA());
        assertFlags(registers, true, true, false, false);
        assertEquals(4, cycles);
    }

    @Test
    public void subA_r_borrow() {
        registers.setA((byte) 0x3E);     //62
        registers.setE((byte) 0X40);     //64

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0xFE, registers.getA());
        assertFlags(registers, false, true, false, true);
        assertEquals(4, cycles);
    }

    @Test
    public void subA_r_borrow_2() {
        registers.setA((byte) 0x01);     //1
        registers.setE((byte) 0XFF);     //255

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x02, registers.getA());
        assertFlags(registers, false, true, true, true);
        assertEquals(4, cycles);
    }

    @Test
    public void subA_r_borrow_3() {
        registers.setA((byte) 0x00);     //0
        registers.setE((byte) 0XFF);     //255

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, true, true, true);
        assertEquals(4, cycles);
    }

    @Test
    public void subA_r_half_borrow() {
        registers.setA((byte) 0x3E);     //0
        registers.setE((byte) 0X0F);     //255

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x2F, registers.getA());
        assertFlags(registers, false, true, true, false);
        assertEquals(4, cycles);
    }

    @Test
    public void subA_n_no_borrow() {
        registers.setA((byte) 0xBA);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .withImmediate8b((byte) 0x01)
                .build();

        short cycles = Arithmetic8b.sub_n(inst, memory, registers);

        assertEquals8(0xB9, registers.getA());    //Result is 8
        assertFlags(registers, false, true, false, false);
        assertEquals(8, cycles);
    }

    @Test
    public void subA_n_zero() {
        registers.setA((byte) 0xBA);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .withImmediate8b((byte) 0xBA)
                .build();

        short cycles = Arithmetic8b.sub_n(inst, memory, registers);

        assertEquals8(0x0, registers.getA());    //Result is 8
        assertFlags(registers, true, true, false, false);
        assertEquals(8, cycles);
    }

    @Test
    public void subA_n_borrow() {
        registers.setA((byte) 0x3E);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .withImmediate8b((byte) 0x40)
                .build();

        short cycles = Arithmetic8b.sub_n(inst, memory, registers);

        assertEquals8(0xFE, registers.getA());    //Result is 8
        assertFlags(registers, false, true, false, true);
        assertEquals(8, cycles);
    }

    @Test
    public void subA_n_half_borrow() {
        registers.setA((byte) 0x3E);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte) 0x0F)
                .build();

        short cycles = Arithmetic8b.sub_n(inst, memory, registers);

        assertEquals8(0x2f, registers.getA());    //Result is 8
        assertFlags(registers, false, true, true, false);
        assertEquals(8, cycles);
    }

    @Test
    public void subA_HL_no_borrow() {
        registers.setA((byte) 0xBA);
        memory.write((char) 0xBEBA, (byte) 0x01);
        registers.setHL((char) 0xBEBA);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.sub_HL(inst, memory, registers);

        assertEquals8(0xB9, registers.getA());    //Result is 8
        assertFlags(registers, false, true, false, false);
        assertEquals(8, cycles);
    }

    @Test
    public void subA_HL_zero() {
        registers.setA((byte) 0xBA);
        memory.write((char) 0xBEBA, (byte) 0xBA);
        registers.setHL((char) 0xBEBA);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.sub_HL(inst, memory, registers);

        assertEquals8(0x00, registers.getA());    //Result is 8
        assertFlags(registers, true, true, false, false);
        assertEquals(8, cycles);
    }

    @Test
    public void subA_HL_borrow() {
        registers.setA((byte) 0x3E);
        memory.write((char) 0xBEBA, (byte) 0x40);
        registers.setHL((char) 0xBEBA);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.sub_HL(inst, memory, registers);

        assertEquals8(0xFE, registers.getA());    //Result is 8
        assertFlags(registers, false, true, false, true);
        assertEquals(8, cycles);
    }

    @Test
    public void subA_HL_half_borrow() {
        registers.setA((byte) 0x3E);
        memory.write((char) 0xBEBA, (byte) 0x0F);
        registers.setHL((char) 0xBEBA);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.sub_HL(inst, memory, registers);

        assertEquals8(0x2F, registers.getA());    //Result is 8
        assertFlags(registers, false, true, true, false);
        assertEquals(8, cycles);
    }

    @Test
    public void subcA_r_no_borrow() {
        registers.setA((byte) 0xBA);
        registers.setE((byte) 0x01);
        registers.setFlagC();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x3)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0xB8, registers.getA());    //Result is 8
        assertFlags(registers, false, true, false, false);
        assertEquals(4, cycles);
    }

    @Test
    public void subAc_r_zero() {
        registers.setA((byte) 0xBA);
        registers.setE((byte) 0xB9);
        registers.setFlagC();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x3)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x0, registers.getA());
        assertFlags(registers, true, true, false, false);
        assertEquals(4, cycles);
    }

    @Test
    public void subAc_r_borrow() {
        registers.setA((byte) 0x3E);     //62
        registers.setE((byte) 0X3F);     //63
        registers.setFlagC();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x3)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0xFE, registers.getA());
        assertFlags(registers, false, true, false, true);
        assertEquals(4, cycles);
    }

    @Test
    public void subAc_r_borrow_2() {
        registers.setA((byte) 0x01);     //1
        registers.setE((byte) 0XFE);     //255
        registers.setFlagC();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x3)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x02, registers.getA());
        assertFlags(registers, false, true, true, true);
        assertEquals(4, cycles);
    }

    @Test
    public void subAc_r_borrow_3() {
        registers.setA((byte) 0x00);     //0
        registers.setE((byte) 0XFE);     //255
        registers.setFlagC();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x3)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, true, true, true);
        assertEquals(4, cycles);
    }

    @Test
    public void subAc_r_half_borrow() {
        registers.setA((byte) 0x3E);     //0
        registers.setE((byte) 0X0E);     //255
        registers.setFlagC();

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x3)
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.sub_r(inst, memory, registers);

        assertEquals8(0x2F, registers.getA());
        assertFlags(registers, false, true, true, false);
        assertEquals(4, cycles);
    }

    @Test
    public void addA_r_no_overflow() {
        registers.setA((byte) 0x5);
        registers.setE((byte) 0x3);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);
        assertEquals8(0x1, registers.getA());    //1
        assertFlags(registers, false, false, true, true);  //C-flag activated
        assertEquals(4, cycles);
    }

    @Test
    public void addA_r_big_overflow() {
        registers.setA((byte) 0x1);     //1
        registers.setE((byte) 0xFF);    //255

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);
        assertEquals8(0x0, registers.getA());    //1
        assertFlags(registers, true, false, true, true);  //C-flag activated
        assertEquals(4, cycles);
    }

    @Test
    public void addA_r_big_overflow_2() {
        registers.setA((byte) 0xFF);     //255
        registers.setE((byte) 0xFF);    //255

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);
        assertEquals8(0xFE, registers.getA());    //1
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

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);
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

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);
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

        short cycles = Arithmetic8b.addA_n(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_n(inst, memory, registers);
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

        short cycles = Arithmetic8b.addA_n(inst, memory, registers);
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

        short cycles = Arithmetic8b.addA_n(inst, memory, registers);
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

        short cycles = Arithmetic8b.addA_HL(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);

        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, true);
        assertEquals(4, cycles);
    }

    @Test
    public void addc_A_r_half_overflow() {

        registers.setA((byte) 0xE1);
        registers.setE((byte) 0x1E);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.addA_r(inst, memory, registers);

        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, true);
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

        short cycles = Arithmetic8b.addA_n(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_n(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_n(inst, memory, registers);

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
                .withImmediate8b((byte) 0x1E)
                .build();

        short cycles = Arithmetic8b.addA_n(inst, memory, registers);

        assertEquals((byte) 0x00, registers.getA());
        assertFlags(registers, true, false, true, true);
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

        short cycles = Arithmetic8b.addA_HL(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_HL(inst, memory, registers);

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

        short cycles = Arithmetic8b.addA_HL(inst, memory, registers);

        assertEquals((byte) 0x00, registers.getA());
        assertFlags(registers, true, false, true, true);
        assertEquals(8, cycles);
    }

    @Test
    public void addc_A_HL_half_overflow() {

        registers.setA((byte) 0xE1);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x1E);
        registers.setFlagC(); //Set carry

        Instruction inst = new InstructionBuilder()
                .withFirstOperand((byte) 0x1)    //To indicate that operation is with carry
                .build();

        short cycles = Arithmetic8b.addA_HL(inst, memory, registers);

        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, true);
        assertEquals(8, cycles);
    }

    @Test
    public void and_r_not_zero() {
        registers.setA((byte) 0x5A);
        registers.setE((byte) 0x3F);
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.and_r(inst, memory, registers);
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

        short cycles = Arithmetic8b.and_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, false);
    }

    @Test
    public void and_n_not_zero() {
        registers.setA((byte) 0x5A);
        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte) 0x3F)
                .build();

        short cycles = Arithmetic8b.and_n(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x1A, registers.getA());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void and_n_zero() {
        registers.setA((byte) 0xF0);
        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte) 0x0F)
                .build();

        short cycles = Arithmetic8b.and_n(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, true, false);
    }

    @Test
    public void and_HL_not_zero() {
        registers.setA((byte) 0x5A);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x3F);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.and_HL(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x1A, registers.getA());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void and_HL_zero() {
        registers.setA((byte) 0xF0);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x0F);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.and_HL(inst, memory, registers);
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

        short cycles = Arithmetic8b.or_r(inst, memory, registers);
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

        short cycles = Arithmetic8b.or_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void or_n_not_zero() {
        registers.setA((byte) 0xF0);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte) 0x0F)
                .build();

        short cycles = Arithmetic8b.or_n(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void or_n_zero() {
        registers.setA((byte) 0x00);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte) 0x00)
                .build();

        short cycles = Arithmetic8b.or_n(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void or_HL_not_zero() {
        registers.setA((byte) 0xF0);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x0F);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.or_HL(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void or_HL_zero() {
        registers.setA((byte) 0x00);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x00);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = Arithmetic8b.or_HL(inst, memory, registers);
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

        short cycles = Arithmetic8b.xor_r(inst, memory, registers);
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

        short cycles = Arithmetic8b.xor_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }


    @Test
    public void xor_n_not_zero() {
        registers.setA((byte) 0x3F);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte) 0x3E)
                .build();

        short cycles = Arithmetic8b.xor_n(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void xor_n_zero() {
        registers.setA((byte) 0x3F);

        Instruction inst = new InstructionBuilder()
                .withImmediate8b((byte) 0x3F)
                .build();

        short cycles = Arithmetic8b.xor_n(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }


    @Test
    public void xor_HL_not_zero() {
        registers.setA((byte) 0x3F);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x3E);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.xor_HL(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x01, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void xor_HL_zero() {
        registers.setA((byte) 0x3F);
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x3F);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.xor_HL(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getA());
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void inc_r() {
        registers.setE((byte) 0x01);
        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();
        short cycles = Arithmetic8b.inc_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x02, registers.getE());
        assertFlags(registers, false, false, false, false);
    }


    @Test
    public void inc_r_overflow() {
        registers.setE((byte) 0xFF);
        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();
        short cycles = Arithmetic8b.inc_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getE());
        assertFlags(registers, true, false, true, false);
    }

    @Test
    public void inc_r_half_carry() {
        registers.setE((byte) 0x0F);
        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();
        short cycles = Arithmetic8b.inc_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x10, registers.getE());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void inc_r_keeps_flag_c() {
        registers.setE((byte) 0x0F);
        registers.setF((byte) 0xFF);
        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();
        short cycles = Arithmetic8b.inc_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x10, registers.getE());
        assertFlags(registers, false, false, true, true);
    }

    @Test
    public void inc_HL() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x01);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.inc_HL(inst, memory, registers);
        assertEquals(12, cycles);
        assertEquals8(0x02, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void inc_HL_overflow() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xFF);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.inc_HL(inst, memory, registers);
        assertEquals(12, cycles);
        assertEquals8(0x00, memory.read((char) 0xBEBA));
        assertFlags(registers, true, false, true, false);
    }


    @Test
    public void inc_HL_half_carry() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x0f);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.inc_HL(inst, memory, registers);
        assertEquals(12, cycles);
        assertEquals8(0x10, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void inc_HL_keeps_flag_c() {
        registers.setHL((char) 0xBEBA);
        registers.setF((byte) 0xFF);
        memory.write((char) 0xBEBA, (byte) 0xFF);

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = Arithmetic8b.inc_HL(inst, memory, registers);
        assertEquals(12, cycles);
        assertEquals8(0x00, memory.read((char) 0xBEBA));
        assertFlags(registers, true, false, true, true);
    }

    @Test
    public void cpl_zero() {
        registers.setA((byte) 0xFF);
        Instruction inst = new InstructionBuilder()
                .build();
        short cycles = Arithmetic8b.cpl(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getA());
    }

    @Test
    public void cpl_not_zero() {
        registers.setA((byte) 0x0F);
        Instruction inst = new InstructionBuilder()
                .build();
        short cycles = Arithmetic8b.cpl(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xF0, registers.getA());
    }

    @Test
    public void dec_r() {
        registers.setE((byte) 0x01);
        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();
        short cycles = Arithmetic8b.dec_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x00, registers.getE());
        assertFlags(registers, true, true, false, false);
    }

    @Test
    public void dec_r_borrow() {
        registers.setE((byte) 0x00);
        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();
        short cycles = Arithmetic8b.dec_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xFF, registers.getE());
        assertFlags(registers, false, true, true, false);
    }

    @Test
    public void dec_r_borrow_keeps_c() {
        registers.setE((byte) 0x00);
        registers.setF((byte) 0xFF);
        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();
        short cycles = Arithmetic8b.dec_r(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xFF, registers.getE());
        assertFlags(registers, false, true, true, true);
    }


    @Test
    public void dec_HL() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x01);

        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();

        short cycles = Arithmetic8b.dec_HL(inst, memory, registers);
        assertEquals(12, cycles);
        assertEquals8(0x00, memory.read((char) 0xBEBA));
        assertFlags(registers, true, true, false, false);
    }

    @Test
    public void dec_HL_borrow() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x00);

        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();

        short cycles = Arithmetic8b.dec_HL(inst, memory, registers);
        assertEquals(12, cycles);
        assertEquals8(0xFF, memory.read((char) 0xBEBA));
        assertFlags(registers, false, true, true, false);
    }

    @Test
    public void dec_HL_borrow_keeps_c() {
        registers.setHL((char) 0xBEBA);
        registers.setF((byte) 0xFF);

        memory.write((char) 0xBEBA, (byte) 0x00);

        Instruction inst = new InstructionBuilder()
                .withFirstOperand(E)
                .build();

        short cycles = Arithmetic8b.dec_HL(inst, memory, registers);
        assertEquals(12, cycles);
        assertEquals8(0xFF, memory.read((char) 0xBEBA));
        assertFlags(registers, false, true, true, true);
    }

}
