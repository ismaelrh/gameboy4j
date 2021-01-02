package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JumpCommands {

    private static final Logger log = LogManager.getLogger(JumpCommands.class);

    private Registers registers;
    private Memory memory;

    public JumpCommands(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
    }

    /**
     * NOTE:
     * 1st - fetch
     * 2nd - increment PC (depending on data to read)
     * 3rd - execute instruction
     * 4th - wait
     *
     * @param inst
     * @return
     */
    public short jp_nn(Instruction inst) {
        char nn = inst.getImmediate16b();
        registers.setPC(nn);
        return 16;
    }

    public short jp_HL(Instruction inst) {
        char hl = registers.getHL();
        registers.setPC(hl);
        return 4;
    }

    public short jp_f_nn(Instruction inst) {
        if (checkCondition(inst)) {
            registers.setPC(inst.getImmediate16b());
            return 16;
        } else {
            return 12;
        }
    }

    public short jr_PC_dd(Instruction inst) {
        char result = (char) (registers.getPC() + inst.getImmediate8b());
        registers.setPC(result);
        return 12;
    }

    public short jr_f_PC_dd(Instruction inst) {
        if (checkCondition(inst)) {
            char result = (char) (registers.getPC() + inst.getImmediate8b());
            registers.setPC(result);
            return 12;
        } else {
            return 8;
        }
    }

    //Assumes PC has already been moved
    public short call_nn(Instruction inst) {
        doCall(inst.getImmediate16b());
        return 24;
    }

    public short call_f_nn(Instruction inst) {
        if (checkCondition(inst)) {
            doCall(inst.getImmediate16b());
            return 24;
        } else {
            return 12;
        }
    }

    public short ret(Instruction inst) {
        doRet();
        return 16;
    }

    public short ret_f(Instruction inst) {
        if (checkCondition(inst)) {
            doRet();
            return 20;
        } else {
            return 8;
        }
    }

    public short reti(Instruction inst) {
        doRet();
        registers.setIme(true);
        return 16;
    }

    public short rst_n(Instruction inst) {
        char[] addresses = new char[]{0x0000, 0x0008, 0x0010, 0x0018, 0x0020, 0x0028, 0x0030, 0x0038};
        int t = inst.getOpcodeFirstSingleRegister();
        char addressToCall = addresses[t];
        doCall(addressToCall);
        return 16;
    }

    private void doCall(char nn) {
        char pc = registers.getPC();

        //(SP -1) = PCh
        //(SP -2) = PCl
        //PC = nn
        //SP = SP - 2
        byte highByte = (byte) ((pc >> 8) & 0xFF);
        byte lowByte = (byte) ((pc & 0xFF));
        memory.write((char) (registers.getSP() - 1), highByte);
        memory.write((char) (registers.getSP() - 2), lowByte);
        registers.setPC(nn);
        registers.setSP((char) (registers.getSP() - 2));
    }

    private void doRet() {
        char lowPC = (char) ((char) (memory.read(registers.getSP())) & 0x00FF);
        char highPC = (char) ((memory.read((char) (registers.getSP() + 1)) << 8) & 0xFF00);
        char newPC = (char) (highPC | lowPC);
        registers.setPC(newPC);
        registers.setSP((char) (registers.getSP() + 2));
    }


    private boolean checkCondition(Instruction inst) {
        byte condition = (byte) (((inst.getOpcode() & 0x18) >> 3) & 0xFF);
        return (condition == 0 && !registers.checkFlagZ()) ||
                (condition == 1 && registers.checkFlagZ()) ||
                (condition == 2 && !registers.checkFlagC()) ||
                (condition == 3 && registers.checkFlagC());
    }


}
