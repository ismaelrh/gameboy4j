package com.ismaelrh.gameboy.cpu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class Registers {

    private static final Logger log = LogManager.getLogger(Registers.class);

    public final static byte A = 0x07;
    public final static byte B = 0x00;
    public final static byte C = 0x01;
    public final static byte D = 0x02;
    public final static byte E = 0x03;
    public final static byte H = 0x04;
    public final static byte L = 0x05;
    public final static byte BC = 0x00; //00x
    public final static byte DE = 0x01; //01x
    public final static byte HL = 0x02; //10x
    public final static byte AF_SP = 0x03; //11x

    public final static byte NONE = 0xF;

    //Program Counter, 16bit
    private char pc;

    //Stack pointer, 16bit
    private char sp;

    //Accumulator and flags (16bit, contains A and F)
    private char af;

    //BC: 16bit (contains B and C)
    private char bc;

    //DE: 16bit (contains D and E)
    private char de;

    //HL: 16bit (contains H and L)
    private char hl;

    //Interruption master enable flag
    private boolean ime = false;

    private boolean halt = false;

    public Registers() {
    }

    public boolean isHalt() {
        return halt;
    }

    public void setHalt(boolean halt) {
        this.halt = halt;
    }

    public void initForRealGB() {
        this.pc = 0x0100; //PC is initialized at 0x100 (corresponds to ROM BANK)
        this.sp = 0xFFFE; //SP initialized to 0xFFFE on power up, but programmer should not rely on this setting.
        this.af = 0x01B0;
        this.bc = 0x0013;
        this.de = 0x00D8;
        this.hl = 0x014D;
    }

    public void initForTest() {
        this.pc = 0x0100; //PC is initialized at 0x100 (corresponds to ROM BANK)
        this.sp = 0x0000; //SP initialized to 0xFFFE on power up, but programmer should not rely on this setting.
        this.af = 0x0000;
        this.bc = 0x0000;
        this.de = 0x0000;
        this.hl = 0x0000;
    }

    public char getPC() {
        return pc;
    }

    public char getSP() {
        return sp;
    }

    public char getAF() {
        return af;
    }

    public byte getA() {
        return (byte) ((af & 0xFF00) >> 8);
    }

    public byte getF() {
        return (byte) (af & 0xFF);
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
        this.sp = (char) (sp & 0xFFFF);
    }

    public void setAF(char af) {
        this.af = (char)(af & 0xFFF0);
    }

    public void setA(byte a) {
        this.af = (char) ((this.af & 0x00FF) | ((a & 0xFF) << 8));
    }

    /*
     * Explanation why & 0xFF is needed in input.
     * Java bytes are unsigned. That means that, when storing 1000-0000 (80), it is interpreted means -127 instead of 128 for Java.
     * When applying an operator between this and other byte, everything is OK. However, when operating with a larger type (char, it is 2 bytes),
     * it transform the byte to a char, and to keep with the negative, adds 1's at the left (Two-complement). When doing the OR, sets all positions at left to 1's.
     */
    public void setF(byte f) {
        this.af = (char) (((this.af & 0xFF00) | ((f & 0xF0))));
    }

    public void setBC(char bc) {
        this.bc = bc;
    }

    public void setB(byte b) {
        this.bc = (char) ((this.bc & 0x00FF) | ((b & 0xFF) << 8));
    }

    public void setC(byte c) {
        this.bc = (char) ((this.bc & 0xFF00) | (c & 0xFF));
    }

    public void setDE(char de) {
        this.de = de;
    }

    public void setD(byte d) {
        this.de = (char) ((this.de & 0x00FF) | ((d & 0xFF) << 8));
    }

    public void setE(byte e) {
        this.de = (char) ((this.de & 0xFF00) | (e & 0xFF));
    }

    public void setHL(char hl) {
        this.hl = hl;
    }

    public void setH(byte h) {
        this.hl = (char) ((this.hl & 0x00FF) | ((h & 0xFF) << 8));
    }

    public void setL(byte l) {
        this.hl = (char) ((this.hl & 0xFF00) | (l & 0xFF));
    }

    public boolean isIme() {
        return ime;
    }

    public void setIme(boolean ime) {
        this.ime = ime;
    }

    public void clearFlags() {
        this.setF((byte) 0x0);
    }

    public void setFlagZ() {
        this.setF((byte) (this.getF() | 0x80));
    }

    public void clearFlagZ() {
        this.setF((byte) (this.getF() & 0x7F));
    }

    public void setFlagN() {
        this.setF((byte) (this.getF() | 0x40));
    }

    public void clearFlagN() {
        this.setF((byte) (this.getF() & 0xBF));
    }

    public void setFlagH() {
        this.setF((byte) (this.getF() | 0x20));
    }

    public void clearFlagH() {
        this.setF((byte) (this.getF() & 0xDF));
    }

    public void setFlagC() {
        this.setF((byte) (this.getF() | 0x10));
    }

    public void setAllFlags() {
        setFlagZ();
        setFlagN();
        setFlagH();
        setFlagC();
    }

    public void clearFlagC() {
        this.setF((byte) (this.getF() & 0xEF));
    }

    //IMPORTANT: use this functions only for testing and not performance-critical actions
    public boolean checkFlagZ() {
        return ((byte) (this.getF() & 0x80)) != 0x0;
    }

    public boolean checkFlagN() {
        return ((byte) (this.getF() & 0x40)) != 0x0;
    }

    public boolean checkFlagH() {
        return ((byte) (this.getF() & 0x20)) != 0x0;
    }

    public boolean checkFlagC() {
        return ((byte) (this.getF() & 0x10)) != 0x0;
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

    public void setByDoubleCode(byte regCode, char data, boolean useSP) {
        switch (regCode) {
            case BC:
                setBC(data);
                break;
            case DE:
                setDE(data);
                break;
            case HL:
                setHL(data);
                break;
            case AF_SP:
                if (useSP) {
                    setSP(data);
                } else {
                    setAF(data);    //AF is only used in popQQ
                }
                break;
            default:
                log.error("Incorrect store to double register by code: " + String.format("%02x", (int) regCode));
        }
    }

    //This always uses 0x3 as AF
    public char getByDoubleCode(byte regCode, boolean useSP) {
        switch (regCode) {
            case BC:
                return getBC();
            case DE:
                return getDE();
            case HL:
                return getHL();
            case AF_SP:
                if (useSP) {
                    return getSP();
                } else {
                    return getAF();
                }
            default:
                log.error("Incorrect store to double register by code: " + String.format("%02x", (int) regCode));
                return 0x0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registers registers = (Registers) o;
        return pc == registers.pc && sp == registers.sp && af == registers.af && bc == registers.bc && de == registers.de && hl == registers.hl && ime == registers.ime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pc, sp, af, bc, de, hl, ime);
    }
}
