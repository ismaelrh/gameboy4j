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


}

