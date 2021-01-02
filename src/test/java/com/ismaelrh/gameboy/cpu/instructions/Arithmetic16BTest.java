package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.instructions.Arithmetic16b;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.*;
import static com.ismaelrh.gameboy.cpu.Registers.*;
import static org.junit.Assert.assertEquals;

public class Arithmetic16BTest {

    private Arithmetic16b arithmetic16B;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
        arithmetic16B = new Arithmetic16b(registers, memory);
    }

    @Test
    public void inc_rr_BC() {
        registers.setBC((char) 0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, BC, NONE))
                .build();

        short cycles = arithmetic16B.inc_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16((char) 0xCAFF, registers.getBC());
    }

    @Test
    public void inc_rr_DE() {
        registers.setDE((char) 0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, DE, NONE))
                .build();

        short cycles = arithmetic16B.inc_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16((char) 0xCAFF, registers.getDE());
    }

    @Test
    public void inc_rr_HL() {
        registers.setHL((char) 0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, HL, NONE))
                .build();

        short cycles = arithmetic16B.inc_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16((char) 0xCAFF, registers.getHL());
    }

    @Test
    public void inc_rr_SP() {
        registers.setSP((char) 0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, AF_SP, NONE))
                .build();

        short cycles = arithmetic16B.inc_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16((char) 0xCAFF, registers.getSP());
    }

    @Test
    public void dec_rr_BC() {
        registers.setBC((char) 0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, BC, NONE))
                .build();

        short cycles = arithmetic16B.dec_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16((char) 0xCAFD, registers.getBC());
    }

    @Test
    public void dec_rr_DE() {
        registers.setDE((char) 0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, DE, NONE))
                .build();

        short cycles = arithmetic16B.dec_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16((char) 0xCAFD, registers.getDE());
    }

    @Test
    public void dec_rr_HL() {
        registers.setHL((char) 0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, HL, NONE))
                .build();

        short cycles = arithmetic16B.dec_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16((char) 0xCAFD, registers.getHL());
    }

    @Test
    public void dec_rr_SP() {
        registers.setSP((char) 0xCAFE);

        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, AF_SP, NONE))
                .build();

        short cycles = arithmetic16B.dec_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16((char) 0xCAFD, registers.getSP());
    }

    @Test
    public void addHL_rr() {
        registers.setHL((char) 0x8A23);
        registers.setBC((char) 0x0605);

        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, BC, NONE))
                .build();

        short cycles = arithmetic16B.addHL_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16(0x9028, registers.getHL());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void addHL_rr_2() {
        registers.setHL((char) 0x8A23);

        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, HL, NONE))
                .build();

        short cycles = arithmetic16B.addHL_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16(0x1446, registers.getHL());
        assertFlags(registers, false, false, true, true);
    }

    @Test
    public void addHL_rr_keeps_z_flag() {
        registers.setHL((char) 0x8A23);
        registers.setBC((char) 0x0605);

        registers.setFlagZ();

        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, BC, NONE))
                .build();

        short cycles = arithmetic16B.addHL_rr(instruction);
        assertEquals(8, cycles);
        assertEquals16(0x9028, registers.getHL());
        assertFlags(registers, true, false, true, false);
    }

    @Test
    public void addSP_dd() {
        registers.setSP((char) 0xFFF8);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0x2)
                .build();

        short cycles = arithmetic16B.addSP_dd(instruction);
        assertEquals(16, cycles);
        assertEquals16(registers.getSP(), (char) 0xFFFA);
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void addSP_dd_overflow_positive() {

        registers.setSP((char) 0xFFFF);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0x02)
                .build();

        short cycles = arithmetic16B.addSP_dd(instruction);
        assertEquals(16, cycles);
        assertEquals16(0x1, registers.getSP());
        assertFlags(registers, false, false, true, true);
    }

    @Test
    public void addSP_dd_half_overflow_positive() {

        registers.setSP((char) 0xFF0F);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0x01)
                .build();

        short cycles = arithmetic16B.addSP_dd(instruction);
        assertEquals(16, cycles);
        assertEquals16(0xFF10, registers.getSP());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void addSP_dd_negative() {

        registers.setSP((char) 0xFFFF);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0xFF) //Remove 1
                .build();

        //Flags are calculated as adding 255

        short cycles = arithmetic16B.addSP_dd(instruction);
        assertEquals(16, cycles);
        assertEquals16(0xFFFE, registers.getSP());
        assertFlags(registers, false, false, true, true);
    }

    @Test
    public void addSP_dd_negative_half() {

        registers.setSP((char) 0x0F0F);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0x81) //1000 0001, removes 0x7F
                .build();

        //Flags are calculated as adding 129 ->
        short cycles = arithmetic16B.addSP_dd(instruction);
        assertEquals(16, cycles);
        assertEquals16(0xE90, registers.getSP());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void ldHL_SP_dd() {
        registers.setSP((char) 0xFFF8);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0x2)
                .build();

        short cycles = arithmetic16B.loadHL_SPdd(instruction);
        assertEquals(12, cycles);
        assertEquals16(registers.getHL(), (char) 0xFFFA);
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void ldHL_SP_dd_overflow_positive() {

        registers.setSP((char) 0xFFFF);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0x02)
                .build();

        short cycles = arithmetic16B.loadHL_SPdd(instruction);
        assertEquals(12, cycles);
        assertEquals16(0x1, registers.getHL());
        assertFlags(registers, false, false, true, true);
    }

    @Test
    public void ldHL_SP_dd_half_overflow_positive() {

        registers.setSP((char) 0xFF0F);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0x01)
                .build();

        short cycles = arithmetic16B.loadHL_SPdd(instruction);
        assertEquals(12, cycles);
        assertEquals16(0xFF10, registers.getHL());
        assertFlags(registers, false, false, true, false);
    }

    @Test
    public void ldHL_SP_dd_negative() {

        registers.setSP((char) 0xFFFF);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0xFF) //Remove 1
                .build();

        //Flags are calculated as adding 255

        short cycles = arithmetic16B.loadHL_SPdd(instruction);
        assertEquals(12, cycles);
        assertEquals16(0xFFFE, registers.getHL());
        assertFlags(registers, false, false, true, true);
    }


    @Test
    public void ldHL_SP_dd_negative_half() {

        registers.setSP((char) 0x0F0F);

        Instruction instruction = new InstructionBuilder()
                .withImmediate8b((byte) 0x81) //1000 0001, removes 0x7F
                .build();

        //Flags are calculated as adding 129 ->
        short cycles = arithmetic16B.loadHL_SPdd(instruction);
        assertEquals(12, cycles);
        assertEquals16(0xE90, registers.getHL());
        assertFlags(registers, false, false, true, false);
    }



}