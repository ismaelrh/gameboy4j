package com.ismaelrh.gameboy;

public class InstructionBuilder {

    private byte opcode = 0x0;

    private boolean hasImmediate8b;
    private byte immediate8b = 0x0;

    private boolean hasImmediate16b;
    private char immediate16b = 0x0;

    public InstructionBuilder withOpcode(byte opcode) {
        this.opcode = opcode;
        return this;
    }

    //Write only 2 most-significant bits
    public InstructionBuilder withOpcodePrefix(byte opcodePrefix) {
        this.opcode = (byte) (this.opcode & 0x3F);  //Erase first two bits
        this.opcode = (byte) (this.opcode | opcodePrefix << 6); //Set two first bits
        return this;
    }

    public InstructionBuilder withFirstOperand(byte firstOperand) {
        this.opcode = (byte) (this.opcode & 0xC7);  //Erase 1st operand
        this.opcode = (byte) (this.opcode | ((firstOperand & 0x7) << 3));//OP FST 000
        return this;

    }

    public InstructionBuilder withSecondOperand(byte secondOperand) {
        this.opcode = (byte) (this.opcode & 0xF8);  //Erase 2nd operand
        this.opcode = (byte) (this.opcode | ((secondOperand & 0x7)));//OP FST SND
        return this;
    }

    public InstructionBuilder withImmediate8b(byte immediate8b) {
        this.immediate8b = immediate8b;
        this.hasImmediate8b = true;
        return this;
    }

    public InstructionBuilder withImmediate16b(char immediate16b) {
        this.immediate16b = immediate16b;
        this.hasImmediate16b = true;
        return this;
    }

    public Instruction build() {
        Instruction inst = new Instruction(opcode);
        if (hasImmediate8b) {
            inst.setImmediate8b(immediate8b);
        }
        if (hasImmediate16b) {
            inst.setImmediate16b(immediate16b);
        }

        return inst;
    }
}
