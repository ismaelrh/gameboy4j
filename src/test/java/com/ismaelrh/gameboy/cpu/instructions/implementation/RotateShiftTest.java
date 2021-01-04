package com.ismaelrh.gameboy.cpu.instructions.implementation;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertEquals8;
import static com.ismaelrh.gameboy.TestUtils.assertFlags;
import static org.junit.Assert.assertEquals;

public class RotateShiftTest {

    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
    }

    @Test
    public void rlca_1() {
        registers.setA((byte) 0x81); //1000 0001 -> 00000011, C=1

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rlca(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x03, registers.getA());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void rlca_0() {
        registers.setA((byte) 0x02); //0000 0010 -> 0000 0100, C=0

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rlca(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x04, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void rlca_ff() {
        registers.setA((byte) 0xFF); //1111 1111 -> 1111 1111, C=1

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rlca(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void rrca_1() {
        registers.setA((byte) 0x81); //1000 0001 -> 1100 0000, C= 1

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rrca(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xC0, registers.getA());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void rrca_0() {
        registers.setA((byte) 0x80); //1000 0000 -> 0100 0000, C=0

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rrca(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x40, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void rrca_ff() {
        registers.setA((byte) 0xff); //1111 1111 -> 1111 1111, C=1

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rrca(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void rla_sets0() {
        registers.setA((byte) 0x41); // 0100 0001 [1] -> 1000 0011 [0]
        registers.setFlagC();

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rla(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x83, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void rla_sets1() {
        registers.setA((byte) 0xC1); // 1100 0001 [1] -> 1000 0011 [1]
        registers.setFlagC();

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rla(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x83, registers.getA());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void rla_FF() {
        registers.setA((byte) 0xFF); // 1111 1111 [1] -> 1111 1111 [1]
        registers.setFlagC();

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rla(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void rra_sets0() {
        registers.setA((byte) 0x82); // 1000 0010 [1] -> 1100 0001 [0]
        registers.setFlagC();

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rra(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xC1, registers.getA());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void rra_sets1() {
        registers.setA((byte) 0x81); // 1000 0001 [0] -> 0100 0000 [1]

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rra(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0x40, registers.getA());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void rra_ff() {
        registers.setA((byte) 0xFF); // 1111 1111 [1] -> 1111 1111 [1]
        registers.setFlagC();

        Instruction inst = new InstructionBuilder().build();
        short cycles = RotateShift.rra(inst, memory, registers);
        assertEquals(4, cycles);
        assertEquals8(0xFF, registers.getA());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void swap_r() {
        registers.setB((byte) 0xAF);

        //Set flags to check they are set accordingly
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.B)
                .build();

        short cycles = RotateShift.swap_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0xFA, registers.getB());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void swap_hl() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xAF);


        //Set flags to check they are set accordingly
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = RotateShift.swap_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0xFA, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void swap_r_2() {
        registers.setB((byte) 0xFF);

        //Set flags to check they are set accordingly
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.B)
                .build();

        short cycles = RotateShift.swap_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0xFF, registers.getB());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void swap_hl_2() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0xFF);


        //Set flags to check they are set accordingly
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = RotateShift.swap_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0xFF, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void swap_r_zero() {
        registers.setB((byte) 0x00);

        //Set flags to check they are set accordingly
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.B)
                .build();

        short cycles = RotateShift.swap_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getB());
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void swap_hl_zero() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x00);


        //Set flags to check they are set accordingly
        registers.setAllFlags();

        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = RotateShift.swap_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x00, memory.read((char) 0xBEBA));
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void sla_r_carry() {
        registers.setE((byte) 0x9F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();

        short cycles = RotateShift.sla_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x3E, registers.getE());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void sla_hl_carry() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x9F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = RotateShift.sla_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x3E, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void sla_r_no_carry() {
        registers.setE((byte) 0x1F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();

        short cycles = RotateShift.sla_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x3E, registers.getE());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void sla_hl_no_carry() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x1F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = RotateShift.sla_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x3E, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void sla_r_zero_flag() {
        registers.setE((byte) 0x80);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();

        short cycles = RotateShift.sla_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getE());
        assertFlags(registers, true, false, false, true);
    }

    @Test
    public void sla_hl_zero_flag() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x80);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = RotateShift.sla_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x00, memory.read((char) 0xBEBA));
        assertFlags(registers, true, false, false, true);
    }

    @Test
    public void sra_r_msb_0_carry_0() {
        registers.setE((byte) 0x00);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.sra_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getE());
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void sra_hl_msb_0_carry_0() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x00);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.sra_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x00, memory.read((char) 0xBEBA));
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void sra_r_msb_0_carry_1() {
        registers.setE((byte) 0x0F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.sra_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x07, registers.getE());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void sra_hl_msb_0_carry_1() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x0F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.sra_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x07, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void sra_r_msb_1_carry_0() {
        registers.setE((byte) 0x80);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.sra_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0xC0, registers.getE());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void sra_hl_msb_1_carry_0() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x80);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.sra_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0xC0, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void sra_r_msb_1_carry_1() {
        registers.setE((byte) 0x8F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.sra_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0xC7, registers.getE());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void sra_hl_msb_1_carry_1() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x8F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.sra_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0xC7, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void srl_r_msb_0_carry_0() {
        registers.setE((byte) 0x00);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.srl_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x00, registers.getE());
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void srl_hl_msb_0_carry_0() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x00);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.srl_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x00, memory.read((char) 0xBEBA));
        assertFlags(registers, true, false, false, false);
    }

    @Test
    public void srl_r_msb_0_carry_1() {
        registers.setE((byte) 0x0F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.srl_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x07, registers.getE());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void srl_hl_msb_0_carry_1() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x0F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.srl_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x07, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void srl_r_msb_1_carry_0() {
        registers.setE((byte) 0x80);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.srl_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x40, registers.getE());
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void srl_hl_msb_1_carry_0() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x80);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.srl_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x40, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void srl_r_msb_1_carry_1() {
        registers.setE((byte) 0x8F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.srl_r(inst, memory, registers);
        assertEquals(8, cycles);
        assertEquals8(0x47, registers.getE());
        assertFlags(registers, false, false, false, true);
    }

    @Test
    public void srl_hl_msb_1_carry_1() {
        registers.setHL((char) 0xBEBA);
        memory.write((char) 0xBEBA, (byte) 0x8F);
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .withSecondOperand(Registers.E)
                .build();
        short cycles = RotateShift.srl_hl(inst, memory, registers);
        assertEquals(16, cycles);
        assertEquals8(0x47, memory.read((char) 0xBEBA));
        assertFlags(registers, false, false, false, true);
    }

}

