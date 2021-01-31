package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.instructions.InstDescription;

/**
 * This totally depends on the instruction, some can have a prefix, some can not,
 * displacement, etc. This will be filled according to each instruction.
 * <p>
 * Opcode is always present
 */
public class Instruction {

    //To get information about the instruction
    private InstDescription description;

    private int instBytes;

    private Byte prefix;

    private byte opcode;

    private byte nn1;

    private byte nn2;

    public Instruction(byte opcode) {
        this.opcode = opcode;
    }

    public Instruction(byte opcode, byte immediate8b) {
        this(opcode);
        setImmediate8b(immediate8b);
    }

    public Instruction(byte opcode, char immediate16b) {
        this(opcode);
        setImmediate16b(immediate16b);
    }

    public InstDescription getDescription() {
        return description;
    }

    public void setDescription(InstDescription description) {
        this.description = description;
    }

    public int getInstBytes() {
        return instBytes;
    }

    public void setInstBytes(int instBytes) {
        this.instBytes = instBytes;
    }

    public Byte getPrefix() {
        return prefix;
    }

    public void setPrefix(Byte prefix) {
        this.prefix = prefix;
    }

    public byte getOpcode() {
        return opcode;
    }

    public void setOpcode(byte opcode) {
        this.opcode = opcode;
    }

    public byte getImmediate8b() {
        return nn1;
    }

    public void setImmediate8b(byte immediate8b) {
        this.nn1 = immediate8b;
    }

    public char getImmediate16b() {
        char low = (char) ((char) (nn1) & 0x00FF);
        char high = (char) ((nn2 << 8) & 0xFF00);
        return (char) (high | low);
    }

    public void setImmediate16b(char immediate16b) {
        byte highByte = (byte) ((immediate16b >> 8) & 0xFF);
        byte lowByte = (byte) ((immediate16b & 0xFF));
        nn2 = highByte;
        nn1 = lowByte;
    }


    public byte getOpcodeFirstSingleRegister() {
        return (byte) ((opcode & 0x38) >> 3);
    }

    public byte getOpcodeFirstDoubleRegister() {
        return (byte) ((opcode & 0x38) >> 4);
    }

    public byte getOpcodeSecondOperand() {
        return (byte) (opcode & 0x7);
    }

    public byte getNn1() {
        return nn1;
    }

    public void setNn1(byte nn1) {
        this.nn1 = nn1;
    }

    public byte getNn2() {
        return nn2;
    }

    public void setNn2(byte nn2) {
        this.nn2 = nn2;
    }
}
