package com.ismaelrh.gameboy.cpu.instructions.implementation;

import com.ismaelrh.gameboy.cpu.instruction.Instruction;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Arithmetic16b {

    private static final Logger log = LogManager.getLogger(Arithmetic16b.class);

    public static short addHL_rr(Instruction inst, Memory memory, Registers registers) {
        char hl = registers.getHL();
        char regData = registers.getByDoubleCode(inst.getOpcodeFirstDoubleRegister(), true);
        char result = (char) (hl + regData);
        registers.setHL(result);
        registers.clearFlagN(); //Reset

        if (carryOnAdd(result, hl, regData)) {
            registers.setFlagC();
        }
        else{
            registers.clearFlagC();
        }
        if (halfCarryOnAdd(hl, regData)) {
            registers.setFlagH();
        }
        else{
            registers.clearFlagH();
        }
        return 8;
    }

    public static short inc_rr(Instruction inst, Memory memory, Registers registers) {
        //Flags are not touched
        byte register = inst.getOpcodeFirstDoubleRegister();
        char regData = registers.getByDoubleCode(register, true);
        char result = (char) (regData + 0x01);
        registers.setByDoubleCode(register, result, true);
        return 8;
    }

    public static short dec_rr(Instruction inst, Memory memory, Registers registers) {
        //Flags are not touched
        byte register = inst.getOpcodeFirstDoubleRegister();
        char regData = registers.getByDoubleCode(register, true);
        char result = (char) (regData - 0x01);
        registers.setByDoubleCode(register, result, true);
        return 8;
    }

    public static short addSP_dd(Instruction inst, Memory memory, Registers registers) {
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

    public static short loadHL_SPdd(Instruction inst, Memory memory, Registers registers) {
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

    private static boolean carryOnAdd(char newValue, char a, char b) {
        //Check FULL CARRY, if result is less than one of the parameters
        return newValue < a || newValue < b;
    }

    private static boolean carryOnAddSPee(char sp, byte ee) {
        /*
         *
         SetHalf(((regs.reg16[SP] & 0x000F) + (static_cast<u8>(val) & 0x0F)) & 0x0010);
         */
        return (char) (((char) (sp & 0x00FF) + (char) (ee & 0x00FF)) & 0x0100) == (char) 0x0100;
    }

    private static boolean halfCarryOnAddSPee(char sp, byte ee) {
        /*
         *     SetHalf(((regs.reg16[SP] & 0x000F) + (static_cast<u8>(val) & 0x0F)) & 0x0010);
         */
        return (char) (((char) (sp & 0x000F) + (char) (ee & 0x000F)) & 0x0010) == (char) 0x0010;
    }

    private static boolean halfCarryOnAdd(char a, char b) {
        //Bit 11
        return (((char) (a & 0xFFF) + (char) (b & 0xFFF)) & (char) 0x1000) == 0x1000;
    }


}