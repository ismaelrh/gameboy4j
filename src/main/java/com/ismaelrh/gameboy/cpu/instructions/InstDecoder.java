package com.ismaelrh.gameboy.cpu.instructions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public class InstDecoder {

    public InstDecoder() {
        init();
    }

    private static final Logger log = LogManager.getLogger(InstDecoder.class);

    //1-byte opcode => 256 possible ops
    private InstDescription[] normalOps = new InstDescription[256];
    private InstDescription[] cbOps = new InstDescription[256];

    public InstDescription getInst(boolean isCB, byte opcode) {
        if (isCB) {
            return getCbInst(opcode);
        } else {
            return getInst(opcode);
        }
    }

    public InstDescription getInst(byte opcode) {
        InstDescription res = normalOps[opcode];
        if (res == null) {
            log.error(String.format("Instruction 0x%02X does not exist!", opcode));
        }
        return res;
    }

    public InstDescription getCbInst(byte opcode) {
        InstDescription res = cbOps[opcode];
        if (res == null) {
            log.error(String.format("Instruction CB 0x%02X does not exist!", opcode));
        }
        return res;
    }

    private void init() {
        for (int opcode = 0; opcode <= 0xFF; opcode++) {
            normalOps[opcode] = getMatchingInstruction(false, opcode);
            cbOps[opcode] = getMatchingInstruction(true, opcode);
        }
    }

    private InstDescription getMatchingInstruction(boolean isCB, int opcode) {
        String opcodeString = (isCB ? "CB " : "") + String.format("0x%02X", opcode);
        InstDescription bestInst = null;
        for (InstDescription currInst : InstDictionary.descriptions) {
            if ((!isCB && currInst.matches(opcode)) || (isCB && currInst.matchesCB(opcode))) {
                if (bestInst != null && currInst.getNumberOfPlaceholders() == bestInst.getNumberOfPlaceholders()) {
                    log.error("Critical error, opcode " + opcodeString + " matches two instructions! " + currInst.getMnemonic() + " and " + bestInst.getMnemonic());
                }
                if (bestInst == null || currInst.getNumberOfPlaceholders() < bestInst.getNumberOfPlaceholders()) {
                    bestInst = currInst;  //New candidate
                }
            }
        }
        if (bestInst == null) {
            log.error("Critical error, opcode " + opcodeString + " does not match any instruction!");
        }
        return bestInst;
    }


}
