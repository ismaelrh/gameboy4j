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
        byte valueToSub = registers.getByCode(inst.getOpcodeSecondOperand());
        byte oldValue = registers.getA();
        subToA(registers, oldValue, valueToSub, inst.getOpcodeFirstOperand() == 0x3, inst.getOpcodeFirstOperand() == 0x7);
        return 4;
    }

    public short sub_n(Instruction inst) {
        byte valueToSub = inst.getImmediate8b();
        byte oldValue = registers.getA();
        subToA(registers, oldValue, valueToSub, inst.getOpcodeFirstOperand() == 0x3, inst.getOpcodeFirstOperand() == 0x7);
        return 8;
    }

    public short sub_HL(Instruction inst) {
        byte valueToSub = memory.read(registers.getHL());
        byte oldValue = registers.getA();
        subToA(registers, oldValue, valueToSub, inst.getOpcodeFirstOperand() == 0x3, inst.getOpcodeFirstOperand() == 0x7);
        return 8;
    }

    public short and_r(Instruction inst) {
        byte valueToAnd = registers.getByCode(inst.getOpcodeSecondOperand());
        and(valueToAnd);
        return 4;
    }

    public short and_n(Instruction inst) {
        byte valueToAnd = inst.getImmediate8b();
        and(valueToAnd);
        return 8;
    }

    public short and_HL(Instruction inst) {
        char memAddress = registers.getHL();
        byte valueToAnd = memory.read(memAddress);
        and(valueToAnd);
        return 8;
    }

    private void and(byte valueToAnd) {
        byte newValue = (byte) (registers.getA() & valueToAnd & 0xFF);
        registers.setA(newValue);

        registers.setF((byte) 0x20); //0010_0000

        if (newValue == (byte) 0x0) {
            registers.setFlagZ();
        }
    }

    public short or_r(Instruction inst) {
        byte valueToAnd = registers.getByCode(inst.getOpcodeSecondOperand());
        or(valueToAnd);
        return 4;
    }

    public short or_n(Instruction inst) {
        byte valueToAnd = inst.getImmediate8b();
        or(valueToAnd);
        return 8;
    }

    public short or_HL(Instruction inst) {
        char memAddress = registers.getHL();
        byte valueToAnd = memory.read(memAddress);
        or(valueToAnd);
        return 8;
    }

    private void or(byte valueToOr) {
        byte newValue = (byte) (registers.getA() | valueToOr & 0xFF);
        registers.setA(newValue);

        registers.resetFlags();

        if (newValue == (byte) 0x0) {
            registers.setFlagZ();
        }
    }

    public short xor_r(Instruction inst) {
        byte valueToXor = registers.getByCode(inst.getOpcodeSecondOperand());
        xor(valueToXor);
        return 4;
    }

    public short xor_n(Instruction inst) {
        byte valueToXor = inst.getImmediate8b();
        xor(valueToXor);
        return 8;
    }

    public short xor_HL(Instruction inst) {
        char memAddress = registers.getHL();
        byte valueToXor = memory.read(memAddress);
        xor(valueToXor);
        return 8;
    }

    private void xor(byte valueToOr) {
        byte newValue = (byte) (registers.getA() ^ valueToOr & 0xFF);
        registers.setA(newValue);

        registers.resetFlags();

        if (newValue == (byte) 0x0) {
            registers.setFlagZ();
        }
    }

    public short cp_r(Instruction inst) {
        return sub_r(inst);
    }

    public short cp_n(Instruction inst) {
        return sub_n(inst);
    }

    public short cp_HL(Instruction inst) {
        return sub_HL(inst);
    }

    public short inc_r(Instruction inst) {
        byte originalValue = registers.getByCode(inst.getOpcodeFirstOperand());
        byte newValue = (byte) (originalValue + (byte) 0x01);
        byte newFlags = getIncrementFlags(originalValue);
        registers.setF(newFlags);
        registers.setByCode(inst.getOpcodeFirstOperand(), newValue);
        return 4;
    }

    public short inc_HL(Instruction inst) {
        char memAddr = registers.getHL();
        byte originalValue = memory.read(memAddr);

        byte newValue = (byte) (originalValue + (byte) 0x01);
        byte newFlags = getIncrementFlags(originalValue);
        registers.setF(newFlags);
        memory.write(memAddr, newValue);
        return 12;
    }

    public short dec_r(Instruction inst) {
        byte originalValue = registers.getByCode(inst.getOpcodeFirstOperand());
        byte newValue = (byte) (originalValue - (byte) 0x01);
        byte newFlags = getDecrementFlags(originalValue);
        registers.setF(newFlags);
        registers.setByCode(inst.getOpcodeFirstOperand(), newValue);
        return 4;
    }

    public short dec_HL(Instruction inst) {
        char memAddr = registers.getHL();
        byte originalValue = memory.read(memAddr);
        byte newValue = (byte) (originalValue - (byte) 0x01);
        byte newFlags = getDecrementFlags(originalValue);
        registers.setF(newFlags);
        memory.write(memAddr, newValue);
        return 12;
    }

    public short cpl(Instruction inst) {
        //Set NH flags to 1, leave the rest untouched
        byte newFlags = (byte) ((registers.getF() | 0x60) & 0xFF);
        byte baseValue = registers.getA();
        byte result = (byte) (baseValue ^ 0xFF);
        registers.setF(newFlags);
        registers.setA(result);
        return 4;
    }

    private void addToA(Registers registers, byte oldValue, byte valueToAdd, boolean checkForCarry) {

        byte addedCarry = 0;
        if (checkForCarry && registers.checkFlagC()) {
            addedCarry = 0x01;
        }

        byte valueToAddWithCarry = (byte) ((valueToAdd + addedCarry) & 0xFF);

        //Set flags
        //TODO: improve so the addition and the flag calculation is done at same time? I doubt this causes any performance improvement.
        registers.resetFlags();

        //1st flags and ops
        byte flags = getOverflowFlagsForAddition(oldValue, valueToAddWithCarry);
        byte newValue = (byte) (registers.getA() + valueToAddWithCarry);

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
        if (halfCarryOnAdd(a, b)) {
            flags = (byte) (flags | 0x20); //Set H
        }

        //Check FULL CARRY, if result is less than one of the parameters
        if (carryOnAdd(newValue, a, b)) {
            flags = (byte) (flags | 0x10); //Set C
        }

        return (byte) (flags & 0xFF);
    }

    /**
     * sub, sub-c and cp are the same operation.
     * The difference is 1st operand:
     * 010 -> sub
     * 011 -> subc
     * 111 -> cp
     */
    private void subToA(Registers registers, byte oldValue, byte valueToSub, boolean checkForCarry, boolean isCp) {

        byte removedCarry = 0;
        if (checkForCarry && registers.checkFlagC()) {
            removedCarry = 0x01;
        }

        //Set flags
        registers.resetFlags();

        //1st flags and ops
        byte valueToSubWithCarry = (byte) ((valueToSub + removedCarry) & 0xFF);
        byte flags = getOverflowFlagsForSubtraction(oldValue, valueToSubWithCarry);
        byte newValue = (byte) ((registers.getA() - valueToSubWithCarry) & 0xFF);

        //Save
        if (!isCp) {
            registers.setA(newValue);
        }

        //Check for 0 flag
        if (newValue == 0x00) {
            flags = (byte) (flags | 0x80); //Set Z
        }
        registers.setF((byte) (registers.getF() | flags));
    }

    private byte getOverflowFlagsForSubtraction(byte a, byte b) {

        byte newValue = (byte) ((a - b) & 0xFF);
        byte flags = 0x40;  //N flag is always set at the beginning

        // If the lower nibble of the original value is less than the lower nibble of what we're subtracting,
        // it'll need a half carry
        if (halfCarryOnSub(a, b)) {
            flags = (byte) (flags | 0x20); //Set H
        }

        if (carryOnSub(newValue, a, b)) {
            flags = (byte) (flags | 0x10); //Set C
        }

        return (byte) (flags & 0xFF);
    }

    private byte getIncrementFlags(byte value) {

        //Clear flags, except last one
        byte newFlags = (byte) (registers.getF() & 0x10);
        byte newValue = (byte) ((value + 0x01) & 0xFF);

        if (halfCarryOnAdd(value, (byte) 0x1)) {
            newFlags = (byte) (newFlags | 0x20);
        }

        if (newValue == 0x00) {
            newFlags = (byte) (newFlags | 0x80);
        }

        return newFlags;
    }

    private byte getDecrementFlags(byte value) {

        //Clear flags, except last one
        byte newFlags = (byte) (registers.getF() & 0x10);
        //Set n to 1
        newFlags = (byte) (newFlags | 0x40);

        byte newValue = (byte) ((value - 0x01) & 0xFF);

        if (halfCarryOnSub(value, (byte) 0x1)) {
            newFlags = (byte) (newFlags | 0x20);
        }

        if (newValue == 0x00) {
            newFlags = (byte) (newFlags | 0x80);
        }

        return newFlags;
    }

    private boolean halfCarryOnAdd(byte a, byte b) {
        return (((byte) (a & 0xF) + (byte) (b & 0xF)) & (byte) 0x10) == 0x10;
    }

    private boolean halfCarryOnSub(byte a, byte b) {
        return (byte) (a & 0x0F) < (byte) (b & 0x0F);
    }

    private boolean carryOnAdd(byte newValue, byte a, byte b) {
        int intResult = (newValue & 0xFF);
        int intA = (a & 0xFF);
        int intB = (b & 0xFF);

        //Check FULL CARRY, if result is less than one of the parameters
        return intResult < intA || intResult < intB;
    }

    private boolean carryOnSub(byte newValue, byte a, byte b) {
        //Java treats all bytes as signed. With this, we have an unsigned int.
        //These three ints are used to check for overflow
        int intResult = (newValue & 0xFF);
        int intA = (a & 0xFF);

        //Check FULL CARRY, if result is higher than the base parameter
        return intResult > intA;
    }
}
