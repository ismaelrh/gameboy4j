package com.ismaelrh.gameboy.cpu.instructions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class InstDecoder {

    private List<String> notExisting = new ArrayList<>(
            Arrays.asList("0xFD", "0xFC", "0xF4", "0xED", "0xEC", "0xEB", "0xE4", "0xE3", "0xDD", "0xDB", "0xD3", "0xCB")
    );

    public InstDecoder() {
        init();
    }

    private static final Logger log = LogManager.getLogger(InstDecoder.class);

    //1-byte opcode => 256 possible ops
    private InstDescription[] normalOps = new InstDescription[256];
    private InstDescription[] cbOps = new InstDescription[256];

    public InstDescription getInst(boolean isCB, byte opcode) {
        int intValue = (int)opcode & 0xFF;
        if (isCB) {
            return getCbInst(intValue);
        } else {
            return getInst(intValue);
        }
    }

    public InstDescription getInst(int opcode) {
        InstDescription res = normalOps[opcode];
        if (res == null) {
            log.error(String.format("Instruction 0x%02X does not exist!", opcode));
        }
        return res;
    }

    public InstDescription getCbInst(int opcode) {
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
        List<InstDescription> matched = new ArrayList<>();
        for (InstDescription currInst : InstDictionary.descriptions) {
            if ((!isCB && currInst.matches(opcode)) || (isCB && currInst.matchesCB(opcode))) {
                matched.add(currInst);
                if (bestInst == null || currInst.getNumberOfPlaceholders() < bestInst.getNumberOfPlaceholders()) {
                    bestInst = currInst;  //New candidate
                }
            }
        }

        if (bestInst == null && !notExisting.contains(opcodeString)) {
            log.error("Critical error, opcode " + opcodeString + " does not match any instruction!");
        }

        final int bestInstPlaceholders = bestInst != null ? bestInst.getNumberOfPlaceholders() : 0;
        if (matched.stream().filter(i -> i.getNumberOfPlaceholders() == bestInstPlaceholders).count() > 1) {
            log.error("Critical error, there are multiple instructions matching opcode " + opcodeString);
        }


        return bestInst;
    }


}
