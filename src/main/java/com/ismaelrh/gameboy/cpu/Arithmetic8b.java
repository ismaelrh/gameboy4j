package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Arithmetic8b {

    private static final Logger log = LogManager.getLogger(Arithmetic8b.class);

    private Registers registers;
    private Memory memory;

    public Arithmetic8b(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
    }

    //add A,r [A = A + r]
    public short addA_r(Instruction inst) {
        byte valueToAdd = registers.getByCode(inst.getOpcodeSecondOperand());
        byte oldValue = registers.getA();
        addToA(registers, oldValue, valueToAdd, inst.getOpcodeFirstOperand() == 0x1);
        return 4;
    }

    //add A,n [A = A + n]
    public short addA_n(Instruction inst) {
        byte valueToAdd = inst.getImmediate8b();
        byte oldValue = registers.getA();
        addToA(registers, oldValue, valueToAdd, inst.getOpcodeFirstOperand() == 0x1);
        return 8;
    }

    //add A,(HL) [A = A + (HL)]
    public short addA_HL(Instruction inst) {
        byte valueToAdd = memory.read(registers.getHL());
        byte oldValue = registers.getA();
        addToA(registers, oldValue, valueToAdd, inst.getOpcodeFirstOperand() == 0x1);
        return 8;
    }

    public short sub_r(Instruction inst) {
        //TODO
        return 4;
    }

    public short sub_n(Instruction inst) {
        //TODO
        return 8;
    }

    public short sub_HL(Instruction inst) {
        //TODO
        return 8;
    }

    public short and_r(Instruction inst) {
        //TODO
        return 4;
    }

    public short and_n(Instruction inst) {
        //TODO
        return 8;
    }

    public short and_HL(Instruction inst) {
        //TODO
        return 8;
    }

    public short xor_r(Instruction inst) {
        //TODO
        return 4;
    }

    public short xor_n(Instruction inst) {
        //TODO
        return 8;
    }

    public short xor_HL(Instruction inst) {
        //TODO
        return 8;
    }

    public short or_r(Instruction inst) {
        //TODO
        return 4;
    }

    public short or_n(Instruction inst) {
        //TODO
        return 8;
    }

    public short or_HL(Instruction inst) {
        //TODO
        return 8;
    }

    public short cp_r(Instruction inst) {
        //TODO
        return 4;
    }

    public short cp_n(Instruction inst) {
        //TODO
        return 8;
    }

    public short cp_HL(Instruction inst) {
        //TODO
        return 8;
    }


    public short inc_r(Instruction inst){
        //TODO
        return 4;
    }

    public short inc_HL(Instruction inst){
        //TODO
        return 12;
    }

    public short dec_r(Instruction inst){
        //TODO
        return 4;
    }

    public short dec_HL(Instruction inst){
        //TODO
        return 12;
    }




    private void addToA(Registers registers, byte oldValue, byte valueToAdd, boolean checkForCarry) {

        byte addedCarry = 0;
        if (checkForCarry && registers.checkFlagC()) {
            addedCarry = 0x01;
        }


        //Set flags
        //TODO: improve so the addition and the flag calculation is done at same time? I doubt this causes any performance improvement.
        registers.resetFlags();

        //1st flags and ops
        byte flags = getOverflowFlagsForAddition(oldValue, valueToAdd);
        byte newValue = (byte) (registers.getA() + valueToAdd);

        //2nd flags and ops
        if (addedCarry != 0x00) {   //So if any of them sets a flag, it is set
            flags = (byte) (flags | getOverflowFlagsForAddition(newValue, addedCarry));
        }
        newValue = (byte) (newValue + addedCarry);

        //Save
        registers.setA(newValue);

        //Check for 0 flag
        if (newValue == 0x00) {
            flags = (byte) (flags | 0x80); //Set Z
        }
        registers.setF((byte) (registers.getF() | flags));
    }

    private byte getOverflowFlagsForAddition(byte a, byte b) {
        byte newValue = (byte) ((a + b) & 0xFF);
        byte flags = 0x00;

        //Check for half-carry
        if ((((byte) (a & 0xF) + (byte) (b & 0xF)) & (byte) 0x10) == 0x10) {
            flags = (byte) (flags | 0x20); //Set H
        }

        //Java treats all bytes as signed. With this, we have an unsigned int.
        //These three ints are used to check for overflow
        int intResult = (newValue & 0xFF);
        int intA = (a & 0xFF);
        int intB = (b & 0xFF);

        //Check FULL CARRY, if result is less than one of the parameters
        if (intResult < intA || intResult < intB) {
            flags = (byte) (flags | 0x10); //Set C
        }

        return (byte) (flags & 0xFF);
    }
}
