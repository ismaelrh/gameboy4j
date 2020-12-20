package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SingleBit {

    private static final Logger log = LogManager.getLogger(SingleBit.class);

    private Registers registers;
    private Memory memory;

    static int[] indexToMask = new int[]{0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};

    public SingleBit(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
    }

    public short bit_n_r(Instruction inst) {
        int n = (inst.getOpcodeFirstSingleRegister() & 0xFF);
        bit_n(n, registers.getByCode(inst.getOpcodeSecondOperand()));
        return 8;
    }

    public short bit_n_HL(Instruction inst) {
        int n = (inst.getOpcodeFirstSingleRegister() & 0xFF);
        bit_n(n, memory.read(registers.getHL()));
        return 12;
    }

    public short set_n_r(Instruction inst) {
        int n = (inst.getOpcodeFirstSingleRegister() & 0xFF);
        byte res = set_n(n, registers.getByCode(inst.getOpcodeSecondOperand()));
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    public short set_n_HL(Instruction inst) {
        int n = (inst.getOpcodeFirstSingleRegister() & 0xFF);
        byte res = set_n(n, memory.read(registers.getHL()));
        memory.write(registers.getHL(), res);
        return 16;
    }

    public short res_n_r(Instruction inst) {
        int n = (inst.getOpcodeFirstSingleRegister() & 0xFF);
        byte res = reset_n(n, registers.getByCode(inst.getOpcodeSecondOperand()));
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    public short res_n_HL(Instruction inst) {
        int n = (inst.getOpcodeFirstSingleRegister() & 0xFF);
        byte res = reset_n(n, memory.read(registers.getHL()));
        memory.write(registers.getHL(), res);
        return 16;
    }

    private void bit_n(int n, byte data) {

        //Clear first three flags, then set h to 1 (x01x)
        registers.setF((byte) ((registers.getF() & 0x1F) | (0x20)));

        byte maskedData = (byte) (data & (indexToMask[n] & 0xFF));

        if (maskedData == 0) {  //Byte was unset
            registers.setFlagZ();
        }
        //Else, do nothing, as we already cleared the flag
    }

    private byte set_n(int n, byte data) {
        //Flags are not touched
        return (byte) (data | (indexToMask[n] & 0xFF));
    }


    private byte reset_n(int n, byte data) {
        return (byte) ((data & ~indexToMask[n]) & 0xFF);
    }

}
