package com.ismaelrh.gameboy.cpu.instructions.implementation;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.*;
import static org.junit.Assert.*;

public class JumpCommandsTest {

    private JumpCommands jumpCommands;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
    }


    @Test
    public void jpNN() {
        registers.setPC((char) 0xBEBA);
        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).build();
        short cycles = JumpCommands.jp_nn(inst,memory,registers);
        assertEquals(16, cycles);
        assertEquals16(0xCAFE, registers.getPC());
    }

    @Test
    public void jpHL() {
        registers.setPC((char) 0xBEBA);
        registers.setHL((char) 0xCAFE);
        Instruction inst = new InstructionBuilder().build();

        short cycles = JumpCommands.jp_HL(inst,memory,registers);
        assertEquals(4, cycles);
        assertEquals16(0xCAFE, registers.getPC());
    }

    @Test
    //condition 00 => z must be 0
    public void jp_f_nn_00_not_met() {
        registers.setPC((char) 0xBEBA);
        registers.setFlagZ();   //So condition is not met

        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).
                withFirstOperand((byte) 0)
                .build();
        short cycles = JumpCommands.jp_f_nn(inst,memory,registers);


        assertEquals(12, cycles);
        assertEquals16(0xBEBA, registers.getPC());
    }

    @Test
    //condition 00 => z must be 0
    public void jp_f_nn_00__met() {
        registers.setPC((char) 0xBEBA);
        registers.clearFlagZ();   //So condition is met

        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).
                withFirstOperand((byte) 0)
                .build();
        short cycles = JumpCommands.jp_f_nn(inst,memory,registers);


        assertEquals(16, cycles);
        assertEquals16(0xCAFE, registers.getPC());
    }

    @Test
    //condition 01 => z must be 1
    public void jp_f_nn_01_not_met() {
        registers.setPC((char) 0xBEBA);
        registers.clearFlagZ();   //So condition is not met

        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).
                withFirstOperand((byte) 1)
                .build();
        short cycles = JumpCommands.jp_f_nn(inst,memory,registers);


        assertEquals(12, cycles);
        assertEquals16(0xBEBA, registers.getPC());
    }

    @Test
    //condition 01 => z must be 1
    public void jp_f_nn_01_met() {
        registers.setPC((char) 0xBEBA);
        registers.setFlagZ();   //So condition is met

        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).
                withFirstOperand((byte) 1)
                .build();
        short cycles = JumpCommands.jp_f_nn(inst,memory,registers);


        assertEquals(16, cycles);
        assertEquals16(0xCAFE, registers.getPC());
    }


    @Test
    //condition 10 => c must be 0
    public void jp_f_nn_10_not_met() {
        registers.setPC((char) 0xBEBA);
        registers.setFlagC();   //So condition is not met

        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).
                withFirstOperand((byte) 2)
                .build();
        short cycles = JumpCommands.jp_f_nn(inst,memory,registers);


        assertEquals(12, cycles);
        assertEquals16(0xBEBA, registers.getPC());
    }

    @Test
    //condition 10 => c must be 0
    public void jp_f_nn_10_met() {
        registers.setPC((char) 0xBEBA);
        registers.clearFlagC();   //So condition is met

        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).
                withFirstOperand((byte) 2)
                .build();
        short cycles = JumpCommands.jp_f_nn(inst,memory,registers);


        assertEquals(16, cycles);
        assertEquals16(0xCAFE, registers.getPC());
    }

    @Test
    //condition 11 => c must be 1
    public void jp_f_nn_11_not_met() {
        registers.setPC((char) 0xBEBA);
        registers.clearFlagC();   //So condition is not met

        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).
                withFirstOperand((byte) 3)
                .build();
        short cycles = JumpCommands.jp_f_nn(inst,memory,registers);


        assertEquals(12, cycles);
        assertEquals16(0xBEBA, registers.getPC());
    }

    @Test
    //condition 11 => c must be 1
    public void jp_f_nn_11_met() {
        registers.setPC((char) 0xBEBA);
        registers.setFlagC();   //So condition is met

        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).
                withFirstOperand((byte) 3)
                .build();
        short cycles = JumpCommands.jp_f_nn(inst,memory,registers);


        assertEquals(16, cycles);
        assertEquals16(0xCAFE, registers.getPC());
    }

    @Test
    public void pc_dd_subtract() {
        registers.setPC((char) 0xBEBA);
        Instruction inst = new InstructionBuilder().withImmediate8b((byte) -3).build();
        short cycles = JumpCommands.jr_PC_dd(inst,memory,registers);
        assertEquals(12, cycles);
        assertEquals16(0xBEB7, registers.getPC());
    }

    @Test
    public void pc_dd_add() {
        registers.setPC((char) 0xBEBA);
        Instruction inst = new InstructionBuilder().withImmediate8b((byte) +3).build();
        short cycles = JumpCommands.jr_PC_dd(inst,memory,registers);
        assertEquals(12, cycles);
        assertEquals16(0xBEBD, registers.getPC());
    }

    @Test
    public void call_nn() {
        registers.setPC((char) 0xBEBA);
        registers.setSP((char) 0xAAAA);
        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).build();
        short cycles = JumpCommands.call_nn(inst,memory,registers);
        assertEquals(24, cycles);
        assertEquals16(0xCAFE, registers.getPC());
        assertEquals16(0xAAA8, registers.getSP());
        assertEquals8(memory.read((char) 0xAAA9), (byte) 0xBE);
        assertEquals8(memory.read((char) 0xAAA8), (byte) 0xBA);
    }

    @Test
    public void ret() {
        registers.setSP((char) 0xAAA0);
        memory.write((char) 0xAAA0, (byte) 0xBA);
        memory.write((char) 0xAAA1, (byte) 0xBE);
        Instruction inst = new InstructionBuilder().build();

        short cycles = JumpCommands.ret(inst,memory,registers);
        assertEquals(16, cycles);
        assertEquals16(0xBEBA, registers.getPC());
        assertEquals16(0xAAA2, registers.getSP());
    }

    @Test
    public void reti() {
        Instruction inst = new InstructionBuilder().build();
        assertFalse(registers.isIme());
        short cycles = JumpCommands.reti(inst,memory,registers);
        assertEquals(16, cycles);
        assertTrue(registers.isIme());
    }

    @Test
    public void call_ret() {
        registers.setSP((char) 0xAAAA);
        registers.setPC((char) 0xBEBA);
        Instruction inst = new InstructionBuilder().withImmediate16b((char) 0xCAFE).build();

        JumpCommands.call_nn(inst,memory,registers);
        assertEquals16(0xCAFE, registers.getPC());
        JumpCommands.ret(inst,memory,registers);

        assertEquals16(0xAAAA, registers.getSP());
        assertEquals16(0xBEBA, registers.getPC());
    }

    @Test
    public void rst_n_0() {
        //3 is 0x0018
        Instruction inst = new InstructionBuilder().withFirstOperand((byte) 0).build();
        short cycles = JumpCommands.rst_n(inst,memory,registers);
        assertEquals(16, cycles);
        assertEquals16(0x0000, registers.getPC());
    }

    @Test
    public void rst_n_3() {
        //3 is 0x0018
        Instruction inst = new InstructionBuilder().withFirstOperand((byte) 3).build();
        short cycles = JumpCommands.rst_n(inst,memory,registers);
        assertEquals(16, cycles);
        assertEquals16(0x0018, registers.getPC());
    }

    @Test
    public void rst_n_7() {
        //3 is 0x0018
        Instruction inst = new InstructionBuilder().withFirstOperand((byte) 7).build();
        short cycles = JumpCommands.rst_n(inst,memory,registers);
        assertEquals(16, cycles);
        assertEquals16(0x0038, registers.getPC());
    }

}

