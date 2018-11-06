package com.ismaelrh.gameboy.cpu;

public class ProcessUnit {

	//Program Counter, 16bit
	private char pc;

	//Stack pointer, 16bit
	private char sp;

	//Accumulator
	private byte a;

	//Flags
	private byte f;

	//BC: 16bit
	private char bc;

	//DE: 16bit
	private char de;

	//HL: 16bit
	private char hl;

	public ProcessUnit() {
		init();
	}

	public void init() {
		this.pc = 0x0;
		this.sp = 0x0;
		this.a = 0x0;
		this.f = 0x0;
		this.bc = 0x0;
		this.de = 0x0;
		this.hl = 0x0;
	}

	char getPC() {
		return pc;
	}

	char getSP() {
		return sp;
	}

	byte getA() {
		return a;
	}

	byte getF() {
		return f;
	}

	char getBC() {
		return bc;
	}

	byte getB() {
		return (byte) ((bc & 0xFF00) >> 8);
	}

	byte getC() {
		return (byte) (bc & 0xFF);
	}

	char getDE() {
		return de;
	}

	byte getD() {
		return (byte) ((de & 0xFF00) >> 8);
	}

	byte getE() {
		return (byte) (de & 0xFF);
	}

	char getHL() {
		return hl;
	}

	byte getH() {
		return (byte) ((hl & 0xFF00) >> 8);
	}

	byte getL() {
		return (byte) (hl & 0xFF);
	}

	void setPC(char pc) {
		this.pc = pc;
	}

	void setSP(char sp) {
		this.sp = sp;
	}

	void setA(byte a) {
		this.a = a;
	}

	void setF(byte f) {
		this.f = f;
	}

	void setBC(char bc) {
		this.bc = bc;
	}

	void setB(byte b) {
		this.bc = (char) ((this.bc & 0x00FF) | (b << 8));
	}

	void setC(byte c) {
		this.bc = (char) ((this.bc & 0xFF00) | (c));
	}

	void setDE(char de) {
		this.de = de;
	}

	void setD(byte d) {
		this.de = (char) ((this.de & 0x00FF) | (d << 8));
	}

	void setE(byte e) {
		this.de = (char) ((this.de & 0xFF00) | (e));
	}

	void setHL(char hl) {
		this.hl = hl;
	}

	void setH(byte h) {
		this.hl = (char) ((this.hl & 0x00FF) | (h << 8));
	}

	void setL(byte l) {
		this.hl = (char) ((this.hl & 0xFF00) | (l));
	}


}
