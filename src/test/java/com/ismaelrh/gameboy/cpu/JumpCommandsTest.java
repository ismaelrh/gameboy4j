package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.Memory;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertFlags;
import static org.junit.Assert.assertEquals;

public class JumpCommandsTest {

    private JumpCommands jumpCommands;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
        jumpCommands = new JumpCommands(registers, memory);
    }


}

