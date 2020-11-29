package com.ismaelrh.gameboy;

/**
 * This totally depends on the instruction, some can have a prefix, some can not,
 * displacement, etc. This will be filled according to each instruction.
 * <p>
 * Opcode is always present
 */
public class Instruction {

    private byte prefix;

    private byte opcode;

    private short displacement;

    private byte immediate8b;

    private char immediate16b;

    public Instruction(byte opcode) {
        this.opcode = opcode;
    }

    public Instruction(byte opcode, byte immediate8b) {
        this(opcode);
        this.immediate8b = immediate8b;
    }

    public Instruction(byte opcode, char immediate16b) {
        this(opcode);
        this.immediate16b = immediate16b;
    }

    public byte getPrefix() {
        return prefix;
    }

    public void setPrefix(byte prefix) {
        this.prefix = prefix;
    }

    public byte getOpcode() {
        return opcode;
    }

    public void setOpcode(byte opcode) {
        this.opcode = opcode;
    }

    public short getDisplacement() {
        return displacement;
    }

    public void setDisplacement(short displacement) {
        this.displacement = displacement;
    }

    public byte getImmediate8b() {
        return immediate8b;
    }

    public void setImmediate8b(byte immediate8b) {
        this.immediate8b = immediate8b;
    }

    public char getImmediate16b() {
        return immediate16b;
    }

    public void setImmediate16b(char immediate16b) {
        this.immediate16b = immediate16b;
    }

    public byte getOpcodePrefix() {
        return (byte) ((opcode & 0xC0) >> 6);
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


}
