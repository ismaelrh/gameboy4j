package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Memory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadUnit {

	private static final Logger log = LogManager.getLogger(LoadUnit.class);

	private Registers registers;
	private Memory memory;

	public LoadUnit(Registers registers, Memory memory) {
		this.registers = registers;
		this.memory = memory;
	}

	//TODO: indicate that operators return CLOCK CYCLES (1 machine cycle = 4 clock cycles)

	//ld r,r' [r <- r']
	public short loadRR(byte opcode) {
		byte secondOp = getOpcodeSecondOperand(opcode);
		byte firstOp = getOpcodeFirstOperand(opcode);
		byte data = registers.getByCode(secondOp);
		registers.setByCode(firstOp, data);

		return 4;
	}

	//ld r,n  [r <- n]
	public short loadRImmediate(byte opcode, byte immediate) {
		byte operand = getOpcodeFirstOperand(opcode);
		registers.setByCode(operand, immediate);

		return 8;
	}

	//ld r, (HL)  [r<-(HL)]
	public short loadRHL(byte opcode) {
		byte operand = getOpcodeFirstOperand(opcode);
		char addressToRead = registers.getHL();
		byte memData = memory.read(addressToRead);
		registers.setByCode(operand, memData);

		return 8;
	}

	//ld (HL), r  [(HL) <- r]
	public short loadHLR(byte opcode) {
		byte operand = getOpcodeSecondOperand(opcode);
		byte registerData = registers.getByCode(operand);
		char addressToWrite = registers.getHL();
		memory.write(addressToWrite, registerData);

		return 8;
	}

	//ld (HL),n  [(HL) <- n]
	public short loadHLN(byte immediate) {
		char addressToWrite = registers.getHL();
		memory.write(addressToWrite, immediate);

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
	public short loadA_nn(char immediate16) {
		byte memData = memory.read(immediate16);
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
	public short loadNN_A(char immediate16) {
		byte registerData = registers.getA();
		memory.write(immediate16, registerData);

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

	private byte getOpcodeFirstOperand(byte opcode) {
		return (byte) ((opcode & 0x38) >> 3);
	}

	private byte getOpcodeSecondOperand(byte opcode) {
		return (byte) (opcode & 0x7);
	}


}
