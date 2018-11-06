package com.ismaelrh.gameboy.cpu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registers {

	private static final Logger log = LogManager.getLogger(Registers.class);

	public final static byte A = 0x07;
	public final static byte B = 0x00;
	public final static byte C = 0x01;
	public final static byte D = 0x02;
	public final static byte E = 0x03;
	public final static byte H = 0x04;
	public final static byte L = 0x05;
	public final static byte NONE = 0xF;

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

	public Registers() {
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

	public char getPC() {
		return pc;
	}

	public char getSP() {
		return sp;
	}

	public byte getA() {
		return a;
	}

	public byte getF() {
		return f;
	}

	public char getBC() {
		return bc;
	}

	public byte getB() {
		return (byte) ((bc & 0xFF00) >> 8);
	}

	public byte getC() {
		return (byte) (bc & 0xFF);
	}

	public char getDE() {
		return de;
	}

	public byte getD() {
		return (byte) ((de & 0xFF00) >> 8);
	}

	public byte getE() {
		return (byte) (de & 0xFF);
	}

	public char getHL() {
		return hl;
	}

	public byte getH() {
		return (byte) ((hl & 0xFF00) >> 8);
	}

	public byte getL() {
		return (byte) (hl & 0xFF);
	}

	public void setPC(char pc) {
		this.pc = pc;
	}

	public void setSP(char sp) {
		this.sp = sp;
	}

	public void setA(byte a) {
		this.a = a;
	}

	public void setF(byte f) {
		this.f = f;
	}

	public void setBC(char bc) {
		this.bc = bc;
	}

	public void setB(byte b) {
		this.bc = (char) ((this.bc & 0x00FF) | (b << 8));
	}

	public void setC(byte c) {
		this.bc = (char) ((this.bc & 0xFF00) | (c));
	}

	public void setDE(char de) {
		this.de = de;
	}

	public void setD(byte d) {
		this.de = (char) ((this.de & 0x00FF) | (d << 8));
	}

	public void setE(byte e) {
		this.de = (char) ((this.de & 0xFF00) | (e));
	}

	public void setHL(char hl) {
		this.hl = hl;
	}

	public void setH(byte h) {
		this.hl = (char) ((this.hl & 0x00FF) | (h << 8));
	}

	public void setL(byte l) {
		this.hl = (char) ((this.hl & 0xFF00) | (l));
	}

	public byte getByCode(byte regCode) {
		switch (regCode) {
			case A:
				return getA();
			case B:
				return getB();
			case C:
				return getC();
			case D:
				return getD();
			case E:
				return getE();
			case H:
				return getH();
			case L:
				return getL();
			default:
				log.error("Incorrect read register by code: " + String.format("%02x", (int) regCode));
				return 0x0;
		}
	}

	public void setByCode(byte regCode, byte data) {
		switch (regCode) {
			case A:
				setA(data);
				break;
			case B:
				setB(data);
				break;
			case C:
				setC(data);
				break;
			case D:
				setD(data);
				break;
			case E:
				setE(data);
				break;
			case H:
				setH(data);
				break;
			case L:
				setL(data);
				break;
			default:
				log.error("Incorrect store to register by code: " + String.format("%02x", (int) regCode));
		}
	}
}
