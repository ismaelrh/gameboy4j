package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.Memory;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertFlags;
import static org.junit.Assert.assertEquals;

public class ControlCommandsTest {

    private ControlCommands controlCommands;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
        controlCommands = new ControlCommands(registers, memory);
    }

    @Test
    public void nop() {
        registers.setAllFlags();
        Instruction inst = new InstructionBuilder()
                .build();

        short cycles = controlCommands.nop(inst);
        assertEquals(4, cycles);
        assertFlags(registers, true, true, true, true);
    }

    @Test
    public void ccf_value_0() {
        //if CY is 0, flag will be 1
        registers.setF((byte) 0xE0); //(1110) = > E

        Instruction inst = new InstructionBuilder()
                .build();
        short cycles = controlCommands.ccf(inst);
        assertEquals(4, cycles);
        assertFlags(registers, true, false, false, true);

    }

    @Test
    public void ccf_value_1() {
        //if CY is 0, flag will be 1
        registers.setF((byte) 0xF0); //(1111) = > F

        Instruction inst = new InstructionBuilder()
                .build();
        short cycles = controlCommands.ccf(inst);
        assertEquals(4, cycles);
        assertFlags(registers, true, false, false, false);

    }

    @Test
    public void ccf_value_0_first_is_0() {
        //if CY is 0, flag will be 1
        registers.setF((byte) 0x60); //(0110) = > E

        Instruction inst = new InstructionBuilder()
                .build();
        short cycles = controlCommands.ccf(inst);
        assertEquals(4, cycles);
        assertFlags(registers, false, false, false, true);

    }

    @Test
    public void ccf_value_1_first_is_0() {
        //if CY is 0, flag will be 1
        registers.setF((byte) 0x70); //(0111) = > 7

        Instruction inst = new InstructionBuilder()
                .build();
        short cycles = controlCommands.ccf(inst);
        assertEquals(4, cycles);
        assertFlags(registers, false, false, false, false);
    }

    @Test
    public void scf_keep_1() {
        registers.setF((byte) 0xFF);
        Instruction inst = new InstructionBuilder()
                .build();
        short cycles = controlCommands.scf(inst);
        assertEquals(4, controlCommands.scf(inst));
        assertEquals(4, cycles);
        assertFlags(registers, true, false, false, true);
    }

    @Test
    public void scf_keep_0() {
        registers.setF((byte) 0x7F);
        Instruction inst = new InstructionBuilder()
                .build();
        short cycles = controlCommands.scf(inst);
        assertEquals(4, controlCommands.scf(inst));
        assertEquals(4, cycles);
        assertFlags(registers, false, false, false, true);
    }
}

