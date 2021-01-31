package com.ismaelrh.gameboy.cpu.instructions.implementation;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Arithmetic8b {

    private static final Logger log = LogManager.getLogger(Arithmetic8b.class);

    //add A,r [A = A + r]
    public static short addA_r(Instruction inst, Memory memory, Registers registers) {
        byte valueToAdd = registers.getByCode(inst.getOpcodeSecondOperand());
        byte oldValue = registers.getA();
        addToA(registers, oldValue, valueToAdd, inst.getOpcodeFirstSingleRegister() == 0x1);
        return 4;
    }

    //add A,n [A = A + n]
    public static short addA_n(Instruction inst, Memory memory, Registers registers) {
        byte valueToAdd = inst.getImmediate8b();
        byte oldValue = registers.getA();
        addToA(registers, oldValue, valueToAdd, inst.getOpcodeFirstSingleRegister() == 0x1);
        return 8;
    }

    //add A,(HL) [A = A + (HL)]
    public static short addA_HL(Instruction inst, Memory memory, Registers registers) {
        byte valueToAdd = memory.read(registers.getHL());
        byte oldValue = registers.getA();
        addToA(registers, oldValue, valueToAdd, inst.getOpcodeFirstSingleRegister() == 0x1);
        return 8;
    }

    public static short sub_r(Instruction inst, Memory memory, Registers registers) {
        byte valueToSub = registers.getByCode(inst.getOpcodeSecondOperand());
        byte oldValue = registers.getA();
        subToA(registers, oldValue, valueToSub, inst.getOpcodeFirstSingleRegister() == 0x3, inst.getOpcodeFirstSingleRegister() == 0x7);
        return 4;
    }

    public static short sub_n(Instruction inst, Memory memory, Registers registers) {
        byte valueToSub = inst.getImmediate8b();
        byte oldValue = registers.getA();
        subToA(registers, oldValue, valueToSub, inst.getOpcodeFirstSingleRegister() == 0x3, inst.getOpcodeFirstSingleRegister() == 0x7);
        return 8;
    }

    public static short sub_HL(Instruction inst, Memory memory, Registers registers) {
        byte valueToSub = memory.read(registers.getHL());
        byte oldValue = registers.getA();
        subToA(registers, oldValue, valueToSub, inst.getOpcodeFirstSingleRegister() == 0x3, inst.getOpcodeFirstSingleRegister() == 0x7);
        return 8;
    }

    public static short and_r(Instruction inst, Memory memory, Registers registers) {
        byte valueToAnd = registers.getByCode(inst.getOpcodeSecondOperand());
        and(valueToAnd, registers);
        return 4;
    }

    public static short and_n(Instruction inst, Memory memory, Registers registers) {
        byte valueToAnd = inst.getImmediate8b();
        and(valueToAnd, registers);
        return 8;
    }

    public static short and_HL(Instruction inst, Memory memory, Registers registers) {
        char memAddress = registers.getHL();
        byte valueToAnd = memory.read(memAddress);
        and(valueToAnd, registers);
        return 8;
    }

    private static void and(byte valueToAnd, Registers registers) {
        byte newValue = (byte) (registers.getA() & valueToAnd & 0xFF);
        registers.setA(newValue);

        registers.setF((byte) 0x20); //0010_0000

        if (newValue == (byte) 0x0) {
            registers.setFlagZ();
        }
    }

    public static short or_r(Instruction inst, Memory memory, Registers registers) {
        byte valueToAnd = registers.getByCode(inst.getOpcodeSecondOperand());
        or(valueToAnd, registers);
        return 4;
    }

    public static short or_n(Instruction inst, Memory memory, Registers registers) {
        byte valueToAnd = inst.getImmediate8b();
        or(valueToAnd, registers);
        return 8;
    }

    public static short or_HL(Instruction inst, Memory memory, Registers registers) {
        char memAddress = registers.getHL();
        byte valueToAnd = memory.read(memAddress);
        or(valueToAnd, registers);
        return 8;
    }

    private static void or(byte valueToOr, Registers registers) {
        byte newValue = (byte) (registers.getA() | valueToOr & 0xFF);
        registers.setA(newValue);

        registers.clearFlags();

        if (newValue == (byte) 0x0) {
            registers.setFlagZ();
        }
    }

    public static short xor_r(Instruction inst, Memory memory, Registers registers) {
        byte valueToXor = registers.getByCode(inst.getOpcodeSecondOperand());
        xor(valueToXor, registers);
        return 4;
    }

    public static short xor_n(Instruction inst, Memory memory, Registers registers) {
        byte valueToXor = inst.getImmediate8b();
        xor(valueToXor, registers);
        return 8;
    }

    public static short xor_HL(Instruction inst, Memory memory, Registers registers) {
        char memAddress = registers.getHL();
        byte valueToXor = memory.read(memAddress);
        xor(valueToXor, registers);
        return 8;
    }

    private static void xor(byte valueToOr, Registers registers) {
        byte newValue = (byte) (registers.getA() ^ valueToOr & 0xFF);
        registers.setA(newValue);

        registers.clearFlags();

        if (newValue == (byte) 0x0) {
            registers.setFlagZ();
        }
    }

    public static short cp_r(Instruction inst, Memory memory, Registers registers) {
        return sub_r(inst, memory, registers);
    }

    public static short cp_n(Instruction inst, Memory memory, Registers registers) {
        return sub_n(inst, memory, registers);
    }

    public static short cp_HL(Instruction inst, Memory memory, Registers registers) {
        return sub_HL(inst, memory, registers);
    }

    public static short inc_r(Instruction inst, Memory memory, Registers registers) {
        byte originalValue = registers.getByCode(inst.getOpcodeFirstSingleRegister());
        byte newValue = (byte) (originalValue + (byte) 0x01);
        byte newFlags = getIncrementFlags(originalValue, registers);
        registers.setF(newFlags);
        registers.setByCode(inst.getOpcodeFirstSingleRegister(), newValue);
        return 4;
    }

    public static short inc_HL(Instruction inst, Memory memory, Registers registers) {
        char memAddr = registers.getHL();
        byte originalValue = memory.read(memAddr);

        byte newValue = (byte) (originalValue + (byte) 0x01);
        byte newFlags = getIncrementFlags(originalValue, registers);
        registers.setF(newFlags);
        memory.write(memAddr, newValue);
        return 12;
    }

    public static short dec_r(Instruction inst, Memory memory, Registers registers) {
        byte originalValue = registers.getByCode(inst.getOpcodeFirstSingleRegister());
        byte newValue = (byte) (originalValue - (byte) 0x01);
        byte newFlags = getDecrementFlags(originalValue, registers);
        registers.setF(newFlags);
        registers.setByCode(inst.getOpcodeFirstSingleRegister(), newValue);
        return 4;
    }

    public static short dec_HL(Instruction inst, Memory memory, Registers registers) {
        char memAddr = registers.getHL();
        byte originalValue = memory.read(memAddr);
        byte newValue = (byte) (originalValue - (byte) 0x01);
        byte newFlags = getDecrementFlags(originalValue, registers);
        registers.setF(newFlags);
        memory.write(memAddr, newValue);
        return 12;
    }

    public static short cpl(Instruction inst, Memory memory, Registers registers) {
        //Set NH flags to 1, leave the rest untouched
        byte newFlags = (byte) ((registers.getF() | 0x60) & 0xFF);
        byte baseValue = registers.getA();
        byte result = (byte) (baseValue ^ 0xFF);
        registers.setF(newFlags);
        registers.setA(result);
        return 4;
    }

    public static short daa(Instruction inst, Memory memory, Registers registers) {
        /*
         * // note: assumes a is a uint8_t and wraps from 0xff to 0
         * if (!n_flag) {  // after an addition, adjust if (half-)carry occurred or if result is out of bounds
         *   if (c_flag || a > 0x99) { a += 0x60; c_flag = 1; }
         *   if (h_flag || (a & 0x0f) > 0x09) { a += 0x6; }
         * } else {  // after a subtraction, only adjust if (half-)carry occurred
         *   if (c_flag) { a -= 0x60; }
         *   if (h_flag) { a -= 0x6; }
         * }
         * // these flags are always updated
         * z_flag = (a == 0); // the usual z flag
         * h_flag = 0; // h flag is always cleared
         */
        if (!registers.checkFlagN()) {
            if (registers.checkFlagC() || (registers.getA() & 0xFF) > 0x99) {
                registers.setA((byte) (registers.getA() + 0x60));
                registers.setFlagC();
            }
            if (registers.checkFlagH() || (registers.getA() & 0x0F) > 0x09) {
                registers.setA((byte) (registers.getA() + 0x6));
            }
        } else {
            if (registers.checkFlagC()) {
                registers.setA((byte) (registers.getA() - 0x60));
            }
            if (registers.checkFlagH()) {
                registers.setA((byte) (registers.getA() - 0x6));
            }
        }
        if (registers.getA() == 0x00) {
            registers.setFlagZ();
        } else {
            registers.clearFlagZ();
        }
        registers.clearFlagH();

        return 4;
    }

    private static void addToA(Registers registers, byte oldValue, byte valueToAdd, boolean checkForCarry) {

        byte addedCarry = 0;
        if (checkForCarry && registers.checkFlagC()) {
            addedCarry = 0x01;
        }

        registers.clearFlags();

        //1st step: oldvalue + addedcarry -> stepOneValue
        //2nd step: stepOneValue + valueToAdd

        //TODO: improve so the addition and the flag calculation is done at same time? I doubt this causes any performance improvement.

        byte stepOneFlags = getOverflowFlagsForAddition(oldValue, addedCarry);
        byte stepOneValue = (byte) ((oldValue + addedCarry) & 0xFF);

        byte stepTwoFlags = getOverflowFlagsForAddition(stepOneValue, valueToAdd);
        byte newValue = (byte) ((stepOneValue + valueToAdd) & 0xFF);

        //Save
        registers.setA(newValue);

        //If h or c are 1 in one of such operations, then it is in the result also
        byte resFlags = (byte)(stepOneFlags | stepTwoFlags);

        //Check for 0 flag
        if (newValue == 0x00) {
            resFlags = (byte) (resFlags | 0x80); //Set Z
        }
        registers.setF((byte) (registers.getF() | resFlags));
    }


    private static byte getOverflowFlagsForAddition(byte a, byte b) {
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
    private static void subToA(Registers registers, byte oldValue, byte valueToSub, boolean checkForCarry, boolean isCp) {

        byte removedCarry = 0;
        if (checkForCarry && registers.checkFlagC()) {
            removedCarry = 0x01;
        }

        //Set flags
        registers.clearFlags();

        //1st step: oldvalue - addedcarry -> stepOneValue
        //2nd step: stepOneValue - valueToAdd

        //TODO: improve so the addition and the flag calculation is done at same time? I doubt this causes any performance improvement.

        byte stepOneFlags = getOverflowFlagsForSubtraction(oldValue, removedCarry);
        byte stepOneValue = (byte) ((oldValue - removedCarry) & 0xFF);

        byte stepTwoFlags = getOverflowFlagsForSubtraction(stepOneValue, valueToSub);
        byte newValue = (byte) ((stepOneValue - valueToSub) & 0xFF);

        //If h or c are 1 in one of such operations, then it is in the result also
        byte flags = (byte)(stepOneFlags | stepTwoFlags);


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

    private static byte getOverflowFlagsForSubtraction(byte a, byte b) {

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

    private static byte getIncrementFlags(byte value, Registers registers) {

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

    private static byte getDecrementFlags(byte value, Registers registers) {

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

    private static boolean halfCarryOnAdd(byte a, byte b) {
        return (((byte) (a & 0xF) + (byte) (b & 0xF)) & (byte) 0x10) == 0x10;
    }

    private static boolean halfCarryOnSub(byte a, byte b) {
        return (byte) (a & 0x0F) < (byte) (b & 0x0F);
    }

    private static boolean carryOnAdd(byte newValue, byte a, byte b) {
        int intResult = (newValue & 0xFF);
        int intA = (a & 0xFF);
        int intB = (b & 0xFF);

        //Check FULL CARRY, if result is less than one of the parameters
        return intResult < intA || intResult < intB;
    }

    private static boolean carryOnSub(byte newValue, byte a, byte b) {
        //Java treats all bytes as signed. With this, we have an unsigned int.
        //These three ints are used to check for overflow
        int intResult = (newValue & 0xFF);
        int intA = (a & 0xFF);

        //Check FULL CARRY, if result is higher than the base parameter
        return intResult > intA;
    }
}
