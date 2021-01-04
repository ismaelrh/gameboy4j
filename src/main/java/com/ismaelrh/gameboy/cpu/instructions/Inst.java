package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.Registers;

@FunctionalInterface
public interface Inst {

    int apply(Instruction inst, Memory memory, Registers registers) throws Exception;
}
