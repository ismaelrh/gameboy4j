package com.ismaelrh.gameboy.cpu.instructions.implementation;

import com.ismaelrh.gameboy.cpu.instruction.Instruction;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ControlCommands {

    private static final Logger log = LogManager.getLogger(ControlCommands.class);

    public static short nop(Instruction inst, Memory memory, Registers registers) {
        //Does nothing
        //If CY is different to
        return 4;
    }

    public static short ccf(Instruction inst, Memory memory, Registers registers) {

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

    public static short scf(Instruction inst, Memory memory, Registers registers) {
        //Set flags to -001
        registers.setF((byte) ((registers.getF() & 0x80) | (0x10)));
        return 4;
    }

    public static short halt(Instruction inst, Memory memory, Registers registers) throws Exception {
        registers.setHalt(true);
        return 4;
    }

    public static short stop(Instruction inst, Memory memory, Registers registers) throws Exception {
        throw new Exception("Operation STOP not implemented");
    }

    public static short di(Instruction inst, Memory memory, Registers registers) {
        registers.setIme(false);
        return 4;
    }

    public static short ei(Instruction inst, Memory memory, Registers registers) {
        registers.setIme(true);
        return 4;
    }
}
