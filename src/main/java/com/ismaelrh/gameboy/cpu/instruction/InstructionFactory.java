package com.ismaelrh.gameboy.cpu.instruction;

public class InstructionFactory {

    private static Instruction inst = new Instruction((byte) 0);
    private static boolean inUse = false;

    public static Instruction getInstruction(byte opcode) {
        if (inUse) {
            throw new RuntimeException("Error, the only instruction instance allowed is already being used");
        }
        inUse = true;
        inst.setOpcode(opcode);
        return inst;
    }

    public static void releaseInstruction(Instruction inst) {
        inst.clear();
        inUse = false;
    }


}
