package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Opcode is 1 byte.
 * 2nd operand is 3 least significant bits.
 * 1st operand is 4-6 least significant bits
 */
public class Load8b {

    private static final Logger log = LogManager.getLogger(Load8b.class);

    private Registers registers;
    private Memory memory;

    public Load8b(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
    }

    //TODO: indicate that operators return CLOCK CYCLES (1 machine cycle = 4 clock cycles)
    //ld r,r' [r <- r']
    public short loadRR(Instruction inst) {
        byte secondOp = inst.getOpcodeSecondOperand();
        byte firstOp = inst.getOpcodeFirstOperand();
        byte data = registers.getByCode(secondOp);
        registers.setByCode(firstOp, data);
        return 4;
    }

    //ld r,n  [r <- n]
    public short loadRImmediate(Instruction inst) {
        byte operand = inst.getOpcodeFirstOperand();
        registers.setByCode(operand, inst.getImmediate8b());
        return 8;
    }

    //ld r, (HL)  [r<-(HL)]
    public short loadRHL(Instruction inst) {
        byte operand = inst.getOpcodeFirstOperand();
        char addressToRead = registers.getHL();
        byte memData = memory.read(addressToRead);
        registers.setByCode(operand, memData);

        return 8;
    }

    //ld (HL), r  [(HL) <- r]
    public short loadHLR(Instruction inst) {
        byte operand = inst.getOpcodeSecondOperand();
        byte registerData = registers.getByCode(operand);
        char addressToWrite = registers.getHL();
        memory.write(addressToWrite, registerData);

        return 8;
    }

    //ld (HL),n  [(HL) <- n]
    public short loadHLN(Instruction inst) {
        char addressToWrite = registers.getHL();
        memory.write(addressToWrite, inst.getImmediate8b());

        return 12;
    }

    //ld A,(BC) [A <- (BC)]
    public short loadA_BC() {
        char addressToRead = registers.getBC();
        byte memData = memory.read(addressToRead);
        registers.setA(memData);

        return 8;
    }

    //ld A,(DE) [A <- (DE)]
    public short loadA_DE() {
        char addressToRead = registers.getDE();
        byte memData = memory.read(addressToRead);
        registers.setA(memData);

        return 8;
    }

    //ld A, (nn) [A <- (nn)]
    public short loadA_nn(Instruction inst) {
        byte memData = memory.read(inst.getImmediate16b());
        registers.setA(memData);

        return 16;
    }

    //ld (BC), A [(BC) <- A]
    public short loadBC_A() {
        byte registerData = registers.getA();
        char addressToWrite = registers.getBC();
        memory.write(addressToWrite, registerData);

        return 8;
    }

    //ld (DE), A [(DE) <- A]
    public short loadDE_A() {
        byte registerData = registers.getA();
        char addressToWrite = registers.getDE();
        memory.write(addressToWrite, registerData);

        return 8;
    }

    //ld (nn), A [(nn) <- A]
    public short loadNN_A(Instruction inst) {
        byte registerData = registers.getA();
        memory.write(inst.getImmediate16b(), registerData);

        return 16;
    }

    //ld A, (FF00+C) [A <- (FF00+C)]
    public short loadA_C() {
        byte c = registers.getC();
        char addressToRead = (char) (0xFF00 + c);
        byte memData = memory.read(addressToRead);
        registers.setA(memData);

        return 8;
    }

    //ld A, (FF00+n) [A <- (FF00+n)]
    public short loadA_n(Instruction inst) {
        char addressToRead = (char) (0xFF00 + inst.getImmediate8b());
        byte memData = memory.read(addressToRead);
        registers.setA(memData);

        return 12;
    }

    //ld (FF00+C), A [(FF00+C) <- A]
    public short loadC_A() {
        byte regData = registers.getA();
        byte c = registers.getC();
        char addressToWrite = (char) (0xFF00 + c);
        memory.write(addressToWrite, regData);

        return 8;
    }

    //ld (FF00+n), A  [(FF00+n) <- A]
    public short loadN_A(Instruction inst) {
        byte regData = registers.getA();
        char addressToWrite = (char) (0xFF00 + inst.getImmediate8b());
        memory.write(addressToWrite, regData);

        return 12;
    }

    //ld (HLI), A [(HL) <- A; HL <- HL +1]
    public short loadHLI_A() {
        char addressToWrite = registers.getHL();
        byte regData = registers.getA();
        memory.write(addressToWrite, regData);
        registers.setHL((char) (addressToWrite + 1));

        return 8;
    }

    //ld (HLD), A [(HL) <- A; HL <- HL - 1]
    public short loadHLD_A() {
        char addressToWrite = registers.getHL();
        byte regData = registers.getA();
        memory.write(addressToWrite, regData);
        registers.setHL((char) (addressToWrite - 1));

        return 8;
    }

    //LD A, (HLI) [A <- (HL), HL <- HL + 1]
    public short loadA_HLI() {
        char addressToRead = registers.getHL();
        byte memData = memory.read(addressToRead);
        registers.setA(memData);
        registers.setHL((char) (addressToRead + 1));

        return 8;
    }

    //LD A, (HLI) [A <- (HL), HL <- HL - 1]
    public short loadA_HLD() {
        char addressToRead = registers.getHL();
        byte memData = memory.read(addressToRead);
        registers.setA(memData);
        registers.setHL((char) (addressToRead - 1));

        return 8;
    }


}
