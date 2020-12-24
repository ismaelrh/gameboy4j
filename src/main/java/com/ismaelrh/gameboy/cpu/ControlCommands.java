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
        //If CY is different to
        return 4;
    }

    public short ccf(Instruction inst) {

        //Get actual value of CY
        boolean cySet = (registers.getF() & 0x10 & 0xFF) == 0x10;

        //Clear  NHC flags
        registers.setF((byte) (registers.getF() & 0x80));

        //cy=1 => result 0
        //cy=0 => result 1
        if (!cySet) {
            registers.setFlagC();
        }

        return 4;
    }

    public short scf(Instruction inst) {
        //Set flags to -001
        registers.setF((byte)((registers.getF() & 0x80) | (0x10)));
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
