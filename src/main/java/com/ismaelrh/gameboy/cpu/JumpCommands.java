package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
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

}
