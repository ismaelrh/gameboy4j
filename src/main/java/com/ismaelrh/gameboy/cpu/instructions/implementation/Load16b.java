package com.ismaelrh.gameboy.cpu.instructions.implementation;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Opcode is 1 byte.
 * 2nd operand is 3 least significant bits.
 * 1st operand is 4-6 least significant bits
 */
public class Load16b {

    private static final Logger log = LogManager.getLogger(Load16b.class);



    /*
     * 16-BIT LOAD COMMANDS
     */

    //LD rr, nn [rr <- nn] (rr is pair of registers)
    public static short loadRR_NN(Instruction inst, Memory memory, Registers registers) {
        byte regCode = inst.getOpcodeFirstDoubleRegister();
        registers.setByDoubleCode(regCode, inst.getImmediate16b(), true);

        return 12;
    }

    //LD SP, HL (SP <- HL)
    public static short loadSP_HL(Instruction inst, Memory memory, Registers registers) {
        char hlContent = registers.getHL();
        registers.setSP(hlContent);

        return 8;
    }

    //push qq ((SP -1) <- qqH; (SP -2) <- qqL; SP <- SP -2)
    public static short push_QQ(Instruction inst, Memory memory, Registers registers) {
        byte regCode = inst.getOpcodeFirstDoubleRegister();
        char regContent = registers.getByDoubleCode(regCode, false);
        byte high = (byte) ((regContent >> 8) & 0xFF);
        byte low = (byte) (regContent & 0xFF);
        char curSP = registers.getSP();
        memory.write((char) (curSP - 1), high);
        memory.write((char) (curSP - 2), low);
        registers.setSP((char) (curSP - 2));

        return 16;
    }

    //pop qq (qqL <- (SP); qqH <- (SP+1); SP <- SP + 2)
    public static short pop_QQ(Instruction inst, Memory memory, Registers registers) {
        byte regCode = inst.getOpcodeFirstDoubleRegister();
        char spPointer = registers.getSP();
        char spPlusOnePointer = (char) (spPointer + 1);
        byte lowContent = memory.read(spPointer);
        byte highContent = memory.read(spPlusOnePointer);
        char dataToWrite = (char) ((highContent << 8) | (lowContent & 0xFF));
        registers.setByDoubleCode(regCode, dataToWrite, false);
        registers.setSP((char) (spPointer + 0x2));
        return 12;
    }


}
