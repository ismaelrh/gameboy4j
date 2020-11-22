package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.Memory;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertFlags;
import static com.ismaelrh.gameboy.cpu.Registers.E;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ArithmeticCommands8bTest {

    private ArithmeticCommands8b arithmeticCommands8b;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
        arithmeticCommands8b = new ArithmeticCommands8b(registers, memory);
    }

    @Test
    public void addA_r_no_overflow() {
        registers.setA((byte) 0x5);
        registers.setE((byte) 0x3);

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(E)
                .build();

        short cycles = arithmeticCommands8b.addA_r(inst);

        assertFalse(registers.checkFlagZ());
        assertFalse(registers.checkFlagN());
        assertEquals((byte) 0x8, registers.getA());    //Result is 8
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

        short cycles = arithmeticCommands8b.addA_r(inst);
        assertEquals((byte) 0x1, registers.getA());    //1
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

        short cycles = arithmeticCommands8b.addA_r(inst);
        assertEquals((byte) 0x10, registers.getA());    //1
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

        short cycles = arithmeticCommands8b.addA_r(inst);
        assertEquals((byte) 0x0, registers.getA());    //1
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

        short cycles = arithmeticCommands8b.addA_n(inst);

        assertFalse(registers.checkFlagZ());
        assertFalse(registers.checkFlagN());
        assertEquals((byte) 0x8, registers.getA());    //Result is 8
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

        short cycles = arithmeticCommands8b.addA_n(inst);
        assertEquals((byte) 0x1, registers.getA());    //1
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

        short cycles = arithmeticCommands8b.addA_n(inst);
        assertEquals((byte) 0x10, registers.getA());    //1
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

        short cycles = arithmeticCommands8b.addA_n(inst);
        assertEquals((byte) 0x0, registers.getA());    //1
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

        short cycles = arithmeticCommands8b.addA_HL(inst);

        assertEquals((byte) 0x09, registers.getA());    //Result is 0xFF + 0x0A = 0x109 = 0x09 with overflow C and h
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

        short cycles = arithmeticCommands8b.addA_r(inst);

        assertEquals((byte) 0x04, registers.getA());
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

        short cycles = arithmeticCommands8b.addA_r(inst);

        assertEquals((byte) 0x01, registers.getA());
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

        short cycles = arithmeticCommands8b.addA_r(inst);

        assertEquals((byte) 0x00, registers.getA());
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

        short cycles = arithmeticCommands8b.addA_r(inst);

        assertEquals((byte) 0xF1, registers.getA());
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

        short cycles = arithmeticCommands8b.addA_n(inst);

        assertEquals((byte) 0x04, registers.getA());
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

        short cycles = arithmeticCommands8b.addA_n(inst);

        assertEquals((byte) 0x01, registers.getA());
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

        short cycles = arithmeticCommands8b.addA_n(inst);

        assertEquals((byte) 0x00, registers.getA());
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

        short cycles = arithmeticCommands8b.addA_n(inst);

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

        short cycles = arithmeticCommands8b.addA_HL(inst);

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

        short cycles = arithmeticCommands8b.addA_HL(inst);

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

        short cycles = arithmeticCommands8b.addA_HL(inst);

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

        short cycles = arithmeticCommands8b.addA_HL(inst);

        assertEquals((byte) 0xF1, registers.getA());
        assertFlags(registers, false, false, true, false);
        assertEquals(8, cycles);
    }

}
