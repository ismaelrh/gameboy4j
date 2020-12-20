package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Arithmetic16b {

    private static final Logger log = LogManager.getLogger(Arithmetic16b.class);

    private Registers registers;
    private Memory memory;

    public Arithmetic16b(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
    }

    public short addHL_rr(Instruction inst) {
        char hl = registers.getHL();
        char regData = registers.getByDoubleCode(inst.getOpcodeFirstDoubleRegister(), true);
        char result = (char) (hl + regData);
        registers.setHL(result);
        registers.clearFlagN();
        if (carryOnAdd(result, hl, regData)) {
            registers.setFlagC();
        }
        if (halfCarryOnAdd(hl, regData)) {
            registers.setFlagH();
        }
        return 8;
    }

    public short inc_rr(Instruction inst) {
        //Flags are not touched
        byte register = inst.getOpcodeFirstDoubleRegister();
        char regData = registers.getByDoubleCode(register, true);
        char result = (char) (regData + 0x01);
        registers.setByDoubleCode(register, result, true);
        return 8;
    }

    public short dec_rr(Instruction inst) {
        //Flags are not touched
        byte register = inst.getOpcodeFirstDoubleRegister();
        char regData = registers.getByDoubleCode(register, true);
        char result = (char) (regData - 0x01);
        registers.setByDoubleCode(register, result, true);
        return 8;
    }

    public short addSP_dd(Instruction inst) {
        // The half carry & carry flags for this instruction are set by adding the value as an *unsigned* byte to the
        // lower byte of sp. The addition itself is done with the value as a signed byte.

        //Clear all flags. Z and N will remain reset.
        registers.clearFlags();

        //The addition itself is done with the value as a signed byte.
        char sp = registers.getSP();
        byte toAdd = (byte) (inst.getImmediate8b() & 0xFF);
        char result = (char) (sp + toAdd);
        registers.setSP(result);

        //Carry is in 7-bit
        if (carryOnAddSPee(sp, toAdd)) {
            registers.setFlagC();
        }

        //Half-carry
        if (halfCarryOnAddSPee(sp, toAdd)) {
            registers.setFlagH();
        }

        return 16;
    }

    public short loadHL_SPdd(Instruction inst) {
        // The half carry & carry flags for this instruction are set by adding the value as an *unsigned* byte to the
        // lower byte of sp. The addition itself is done with the value as a signed byte.

        //Clear all flags. Z and N will remain reset.
        registers.clearFlags();

        //The addition itself is done with the value as a signed byte.
        char sp = registers.getSP();
        byte toAdd = (byte) (inst.getImmediate8b() & 0xFF);
        char result = (char) (sp + toAdd);
        registers.setHL(result);

        //Carry is in 7-bit
        if (carryOnAddSPee(sp, toAdd)) {
            registers.setFlagC();
        }

        //Half-carry
        if (halfCarryOnAddSPee(sp, toAdd)) {
            registers.setFlagH();
        }

        return 12;
    }

    private boolean carryOnAdd(char newValue, char a, char b) {
        //Check FULL CARRY, if result is less than one of the parameters
        return newValue < a || newValue < b;
    }

    private boolean carryOnAddSPee(char sp, byte ee) {
        /*
         *
         SetHalf(((regs.reg16[SP] & 0x000F) + (static_cast<u8>(val) & 0x0F)) & 0x0010);
         */
        return (char) (((char) (sp & 0x00FF) + (char) (ee & 0x00FF)) & 0x0100) == (char) 0x0100;
    }

    private boolean halfCarryOnAddSPee(char sp, byte ee) {
        /*
         *     SetHalf(((regs.reg16[SP] & 0x000F) + (static_cast<u8>(val) & 0x0F)) & 0x0010);
         */
        return (char) (((char) (sp & 0x000F) + (char) (ee & 0x000F)) & 0x0010) == (char) 0x0010;
    }

    private boolean halfCarryOnAdd(char a, char b) {
        //Bit 11
        return (((char) (a & 0xFFF) + (char) (b & 0xFFF)) & (char) 0x1000) == 0x1000;
    }


}