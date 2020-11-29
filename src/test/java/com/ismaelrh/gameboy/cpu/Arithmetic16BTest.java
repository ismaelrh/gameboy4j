package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.InstructionBuilder;
import com.ismaelrh.gameboy.Memory;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.*;
import static com.ismaelrh.gameboy.cpu.Registers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    public void inc_rr_BC(){
        registers.setBC((char)0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, BC, NONE))
                .build();

        short cycles = arithmetic16B.inc_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16((char)0xCAFF,registers.getBC());
    }

    @Test
    public void inc_rr_DE(){
        registers.setDE((char)0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, DE, NONE))
                .build();

        short cycles = arithmetic16B.inc_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16((char)0xCAFF,registers.getDE());
    }

    @Test
    public void inc_rr_HL(){
        registers.setHL((char)0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, HL, NONE))
                .build();

        short cycles = arithmetic16B.inc_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16((char)0xCAFF,registers.getHL());
    }

    @Test
    public void inc_rr_SP(){
        registers.setSP((char)0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, AF_SP, NONE))
                .build();

        short cycles = arithmetic16B.inc_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16((char)0xCAFF,registers.getSP());
    }

    @Test
    public void dec_rr_BC(){
        registers.setBC((char)0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, BC, NONE))
                .build();

        short cycles = arithmetic16B.dec_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16((char)0xCAFD,registers.getBC());
    }

    @Test
    public void dec_rr_DE(){
        registers.setDE((char)0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, DE, NONE))
                .build();

        short cycles = arithmetic16B.dec_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16((char)0xCAFD,registers.getDE());
    }

    @Test
    public void dec_rr_HL(){
        registers.setHL((char)0xCAFE);
        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, HL, NONE))
                .build();

        short cycles = arithmetic16B.dec_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16((char)0xCAFD,registers.getHL());
    }

    @Test
    public void dec_rr_SP(){
        registers.setSP((char)0xCAFE);

        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, AF_SP, NONE))
                .build();

        short cycles = arithmetic16B.dec_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16((char)0xCAFD,registers.getSP());
    }

   @Test
   public void addHL_rr(){
        registers.setHL((char)0x8A23);
        registers.setBC((char)0x0605);

       Instruction instruction = new InstructionBuilder()
               .withOpcode(getOpcodeDoubleRegister(0x0, BC, NONE))
               .build();

       short cycles = arithmetic16B.addHL_rr(instruction);
       assertEquals(8,cycles);
       assertEquals16(0x9028,registers.getHL());
       assertFlags(registers,false,false,true,false);
   }

    @Test
    public void addHL_rr_2(){
        registers.setHL((char)0x8A23);

        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, HL, NONE))
                .build();

        short cycles = arithmetic16B.addHL_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16(0x1446,registers.getHL());
        assertFlags(registers,false,false,true,true);
    }

    @Test
    public void addHL_rr_keeps_z_flag(){
        registers.setHL((char)0x8A23);
        registers.setBC((char)0x0605);

        registers.setFlagZ();

        Instruction instruction = new InstructionBuilder()
                .withOpcode(getOpcodeDoubleRegister(0x0, BC, NONE))
                .build();

        short cycles = arithmetic16B.addHL_rr(instruction);
        assertEquals(8,cycles);
        assertEquals16(0x9028,registers.getHL());
        assertFlags(registers,true,false,true,false);
    }

}