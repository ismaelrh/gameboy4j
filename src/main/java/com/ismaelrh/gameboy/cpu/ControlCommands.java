package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ControlCommands {

    private static final Logger log = LogManager.getLogger(ControlCommands.class);

    private Registers registers;
    private Memory memory;

    public ControlCommands(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
    }

    public short nop(Instruction inst) {
        //Does nothing
        return 4;
    }

    public short ccf(Instruction inst) {
        //cy=cy xor 1
        //TODO
        return 4;
    }

    public short scf(Instruction inst) {
        //cy=1
        //TODO
        return 4;
    }

    public short halt(Instruction inst) {
        //TODO
        return -1;
    }

    public short stop(Instruction inst) {
        //TODO
        return -1;
    }

    public short di(Instruction inst) {
        //TODO
        return 4;
    }

    public short ei(Instruction inst) {
        //TODO
        return 4;
    }
}
