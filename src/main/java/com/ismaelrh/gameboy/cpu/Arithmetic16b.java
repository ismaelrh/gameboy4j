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
        char regData = registers.getByDoubleCode(inst.getOpcodeFirstDoubleRegister(),true);
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
        char regData = registers.getByDoubleCode(register,true);
        char result = (char) (regData + 0x01);
        registers.setByDoubleCode(register, result, true);
        return 8;
    }

    public short dec_rr(Instruction inst) {
        //Flags are not touched
        byte register = inst.getOpcodeFirstDoubleRegister();
        char regData = registers.getByDoubleCode(register,true);
        char result = (char) (regData - 0x01);
        registers.setByDoubleCode(register, result, true);
        return 8;
    }

    private boolean carryOnAdd(char newValue, char a, char b) {
        //Check FULL CARRY, if result is less than one of the parameters
        return newValue < a || newValue < b;
    }

    private boolean halfCarryOnAdd(char a, char b) {
        //Bit 11
        return (((char) (a & 0xFFF) + (char) (b & 0xFFF)) & (char) 0x1000) == 0x1000;
    }

}