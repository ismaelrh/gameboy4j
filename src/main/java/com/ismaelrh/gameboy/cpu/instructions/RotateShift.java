package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RotateShift {

    private static final Logger log = LogManager.getLogger(RotateShift.class);

    private Registers registers;
    private Memory memory;

    public RotateShift(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
    }

    //Rotate register A left.
    //C <- [7 <- 0] <- [7]
    public short rlca(Instruction inst) {
        registers.setA(
                rotateLeft(
                        registers.getA(),
                        false,
                        false)
        );
        return 4;
    }

    //Rotate register A left through carry.
    //C <- [7 <- 0] <- C
    public short rla(Instruction inst) {
        registers.setA(
                rotateLeft(
                        registers.getA(),
                        true,
                        false)
        );
        return 4;
    }

    //Rotate register A right.
    //[0] -> [7 -> 0] -> C
    public short rrca(Instruction inst) {
        registers.setA(
                rotateRight(
                        registers.getA(),
                        false,
                        false));
        return 4;
    }

    //Rotate register A right through carry.
    //C -> [7 -> 0] -> C
    public short rra(Instruction inst) {
        registers.setA(
                rotateRight(
                        registers.getA(),
                        true,
                        false));
        return 4;
    }

    //Rotate register r8 left.
    //C <- [7 <- 0] <- [7]
    public short rlc_r(Instruction inst) {
        byte res = rotateLeft(
                registers.getByCode(inst.getOpcodeSecondOperand()),
                false,
                true
        );
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    //Rotate byte pointed to by HL left.
    //C <- [7 <- 0] <- [7]
    public short rlc_HL(Instruction inst) {
        byte res = rotateLeft(
                memory.read(registers.getHL()),
                false,
                true
        );
        memory.write(registers.getHL(), res);
        return 16;
    }

    //Rotate bits in register r8 left through carry.
    //C <- [7 <- 0] <- C
    public short rl_r(Instruction inst) {
        byte res = rotateLeft(
                registers.getByCode(inst.getOpcodeSecondOperand()),
                true,
                true
        );
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    //Rotate byte pointed to by HL left through carry.
    //C <- [7 <- 0] <- C
    public short rl_hl(Instruction inst) {
        byte res = rotateLeft(
                memory.read(registers.getHL()),
                true,
                true
        );
        memory.write(registers.getHL(), res);
        return 16;
    }


    //Rotate register r8 right.
    //[0] -> [7 -> 0] -> C
    public short rrc_r(Instruction inst) {
        byte res = rotateRight(
                registers.getByCode(inst.getOpcodeSecondOperand()),
                false,
                true
        );
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    //Rotate byte pointed to by HL right.
    //C <- [7 <- 0] <- [7]
    public short rrc_HL(Instruction inst) {
        byte res = rotateRight(
                memory.read(registers.getHL()),
                false,
                true
        );
        memory.write(registers.getHL(), res);
        return 16;
    }

    //Rotate register r8 right through carry.
    //C -> [7 -> 0] -> C
    public short rr_r(Instruction inst) {
        byte res = rotateRight(
                registers.getByCode(inst.getOpcodeSecondOperand()),
                true,
                true
        );
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }


    //Rotate byte pointed to by HL right through carry.
    //C <- [7 <- 0] <- C
    public short rr_hl(Instruction inst) {
        byte res = rotateRight(
                memory.read(registers.getHL()),
                true,
                true
        );
        memory.write(registers.getHL(), res);
        return 16;
    }

    //swap r
    public short swap_r(Instruction inst) {
        byte res = swap(
                registers.getByCode(inst.getOpcodeSecondOperand())
        );
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    //swap HL
    public short swap_hl(Instruction inst) {
        byte res = swap(
                memory.read(registers.getHL())
        );
        memory.write(registers.getHL(), res);
        return 16;
    }

    private byte swap(byte data) {
        registers.clearFlags();
        byte res = (byte) (((data & 0x0F) << 4) | ((data & 0xF0) >> 4));
        if (res == 0x00) {
            registers.setFlagZ();
        }
        return res;
    }

    public short sla_r(Instruction inst) {
        byte res = sla(
                registers.getByCode(inst.getOpcodeSecondOperand())
        );
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    public short sla_hl(Instruction inst) {
        byte res = sla(
                memory.read(registers.getHL())
        );
        memory.write(registers.getHL(), res);
        return 16;
    }

    public short sra_r(Instruction inst) {
        byte res = sr(
                registers.getByCode(inst.getOpcodeSecondOperand()),
                true
        );
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    public short sra_hl(Instruction inst) {
        byte res = sr(
                memory.read(registers.getHL()),
                true
        );
        memory.write(registers.getHL(), res);
        return 16;
    }

    public short srl_r(Instruction inst) {
        byte res = sr(
                registers.getByCode(inst.getOpcodeSecondOperand()),
                false
        );
        registers.setByCode(inst.getOpcodeSecondOperand(), res);
        return 8;
    }

    public short srl_hl(Instruction inst) {
        byte res = sr(
                memory.read(registers.getHL()),
                false
        );
        memory.write(registers.getHL(), res);
        return 16;
    }

    private byte sla(byte data) {

        //Higher bit is 1, set carry flag. This also clears other flags
        registers.setF((byte) ((data & 0x80) >> 3));

        byte res = (byte) (data << 1);

        if (res == 0x00) {
            registers.setFlagZ();
        }

        return res;
    }

    private byte sr(byte data, boolean keepMsb) {

        //Lower bit is 1, set carry flag. Also clears other flags
        registers.setF((byte) ((data & 0x01) << 4));

        //Rotate, and leave msb as zero
        byte res = (byte) ((data >> 1) & 0x7F);

        //Now, we do OR with only msb of input, if keepMsb=true (for sra)
        if (keepMsb) {
            res = (byte) (res | (data & 0x80));
        }

        if (res == 0x00) {
            registers.setFlagZ();
        }
        return res;
    }

    private byte rotateLeft(byte value, boolean throughCarry, boolean useZflag) {
        byte oldFlags = registers.getF();
        registers.clearFlags();
        byte res = 0;
        if (throughCarry) {
            res = (byte) ((value << 1) | ((oldFlags & 0x10) >> 4));  //Put in least significant bit the carry
        } else {
            res = (byte) ((value << 1) | (((value & 0x80) >> 7) & 0xFF));  //Move byte 7 to 1st position
        }
        registers.setF((byte) ((value & 0x80) >> 3)); //Higher bit is 1, set carry flag
        if (useZflag && res == 0x00) {
            registers.setFlagZ();
        }
        return res;
    }

    private byte rotateRight(byte value, boolean throughCarry, boolean useZflag) {
        byte oldFlags = registers.getF();
        registers.clearFlags();
        byte res = 0;
        if (throughCarry) {
            res = (byte) (((value & 0xFF) >> 1) | ((oldFlags & 0x10) << 3));  //Put in least significant bit the carry
        } else {
            res = (byte) (((value & 0xFF) >> 1) | (((value & 0x01) << 7) & 0xFF));  //Move byte 1 to 7th position
        }
        registers.setF((byte) ((value & 0x01) << 4)); //Lower bit is 1, set carry flag
        if (useZflag && res == 0x00) {
            registers.setFlagZ();
        }
        return res;
    }


}
