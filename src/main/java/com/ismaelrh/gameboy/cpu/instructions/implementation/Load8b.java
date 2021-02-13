package com.ismaelrh.gameboy.cpu.instructions.implementation;

import com.ismaelrh.gameboy.cpu.instruction.Instruction;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Opcode is 1 byte.
 * 2nd operand is 3 least significant bits.
 * 1st operand is 4-6 least significant bits
 */
public class Load8b {

    private static final Logger log = LogManager.getLogger(Load8b.class);


    //TODO: indicate that operators return CLOCK CYCLES (1 machine cycle = 4 clock cycles)
    //ld r,r' [r <- r']
    public static short loadRR(Instruction inst, Memory memory, Registers registers) {
        byte secondOp = inst.getOpcodeSecondOperand();
        byte firstOp = inst.getOpcodeFirstSingleRegister();
        byte data = registers.getByCode(secondOp);
        registers.setByCode(firstOp, data);
        return 4;
    }

    //ld r,n  [r <- n]
    public static short loadRImmediate(Instruction inst, Memory memory, Registers registers) {
        byte operand = inst.getOpcodeFirstSingleRegister();
        registers.setByCode(operand, inst.getImmediate8b());
        return 8;
    }

    //ld r, (HL)  [r<-(HL)]
    public static short loadRHL(Instruction inst, Memory memory, Registers registers) {
        byte operand = inst.getOpcodeFirstSingleRegister();
        char addressToRead = registers.getHL();
        byte memData = memory.read(addressToRead);
        registers.setByCode(operand, memData);

        return 8;
    }

    //ld (HL), r  [(HL) <- r]
    public static short loadHLR(Instruction inst, Memory memory, Registers registers) {
        byte operand = inst.getOpcodeSecondOperand();
        byte registerData = registers.getByCode(operand);
        char addressToWrite = registers.getHL();
        memory.write(addressToWrite, registerData);

        return 8;
    }

    //ld (HL),n  [(HL) <- n]
    public static short loadHLN(Instruction inst, Memory memory, Registers registers) {
        char addressToWrite = registers.getHL();
        memory.write(addressToWrite, inst.getImmediate8b());

        return 12;
    }

    //ld A,(BC) [A <- (BC)]
    public static short loadA_BC(Instruction inst, Memory memory, Registers registers) {
        char addressToRead = registers.getBC();
        byte memData = memory.read(addressToRead);
        registers.setA(memData);

        return 8;
    }

    //ld A,(DE) [A <- (DE)]
    public static short loadA_DE(Instruction inst, Memory memory, Registers registers) {
        char addressToRead = registers.getDE();
        byte memData = memory.read(addressToRead);
        registers.setA(memData);

        return 8;
    }

    //ld A, (nn) [A <- (nn)]
    public static short loadA_nn(Instruction inst, Memory memory, Registers registers) {
        byte memData = memory.read(inst.getImmediate16b());
        registers.setA(memData);

        return 16;
    }

    //ld (BC), A [(BC) <- A]
    public static short loadBC_A(Instruction inst, Memory memory, Registers registers) {
        byte registerData = registers.getA();
        char addressToWrite = registers.getBC();
        memory.write(addressToWrite, registerData);

        return 8;
    }

    //ld (DE), A [(DE) <- A]
    public static short loadDE_A(Instruction inst, Memory memory, Registers registers) {
        byte registerData = registers.getA();
        char addressToWrite = registers.getDE();
        memory.write(addressToWrite, registerData);

        return 8;
    }

    //ld (nn), A [(nn) <- A]
    public static short loadNN_A(Instruction inst, Memory memory, Registers registers) {
        byte registerData = registers.getA();
        memory.write(inst.getImmediate16b(), registerData);

        return 16;
    }

    //ld A, (FF00+C) [A <- (FF00+C)]
    public static short loadA_C(Instruction inst, Memory memory, Registers registers) {
        byte c = registers.getC();
        char addressToRead = (char) (0xFF00 + (c&0xFF));
        byte memData = memory.read(addressToRead);
        registers.setA(memData);

        return 8;
    }

    //ld A, (FF00+n) [A <- (FF00+n)]
    public static short loadA_n(Instruction inst, Memory memory, Registers registers) {
        char addressToRead = (char) (0xFF00 + (inst.getImmediate8b()&0xFF));
        byte memData = memory.read(addressToRead);
        registers.setA(memData);

        return 12;
    }

    //ld (FF00+C), A [(FF00+C) <- A]
    public static short loadC_A(Instruction inst, Memory memory, Registers registers) {
        byte regData = registers.getA();
        byte c = registers.getC();
        char addressToWrite = (char) (0xFF00 + (c&0xFF));
        memory.write(addressToWrite, regData);

        return 8;
    }

    //ld (FF00+n), A  [(FF00+n) <- A]
    public static short loadN_A(Instruction inst, Memory memory, Registers registers) {
        byte regData = registers.getA();
        char addressToWrite = (char) (0xFF00 + (inst.getImmediate8b()&0xFF));
        memory.write(addressToWrite, regData);

        return 12;
    }

    //ld (HLI), A [(HL) <- A; HL <- HL +1]
    public static short loadHLI_A(Instruction inst, Memory memory, Registers registers) {
        char addressToWrite = registers.getHL();
        byte regData = registers.getA();
        memory.write(addressToWrite, regData);
        registers.setHL((char) (addressToWrite + 1));

        return 8;
    }

    //ld (HLD), A [(HL) <- A; HL <- HL - 1]
    public static short loadHLD_A(Instruction inst, Memory memory, Registers registers) {
        char addressToWrite = registers.getHL();
        byte regData = registers.getA();
        memory.write(addressToWrite, regData);
        registers.setHL((char) (addressToWrite - 1));

        return 8;
    }

    //LD A, (HLI) [A <- (HL), HL <- HL + 1]
    public static short loadA_HLI(Instruction inst, Memory memory, Registers registers) {
        char addressToRead = registers.getHL();
        byte memData = memory.read(addressToRead);
        registers.setA(memData);
        registers.setHL((char) (addressToRead + 1));

        return 8;
    }

    //LD A, (HLI) [A <- (HL), HL <- HL - 1]
    public static short loadA_HLD(Instruction inst, Memory memory, Registers registers) {
        char addressToRead = registers.getHL();
        byte memData = memory.read(addressToRead);
        registers.setA(memData);
        registers.setHL((char) (addressToRead - 1));

        return 8;
    }


}
