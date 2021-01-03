package com.ismaelrh.gameboy.cpu.instructions;

import java.util.regex.Pattern;

public class InstDescription {

    private String mnemonic;
    private String expr;
    private boolean cb;
    private Inst inst;
    private int extraBytes;
    private int numberOfPlaceholders;
    private Pattern pattern;

    public InstDescription(String mnemonic, int prefix, String expr, int extraBytes, Inst inst) {
        this.mnemonic = mnemonic;
        this.inst = inst;
        this.cb = prefix == 0xCB;
        this.expr = expr;
        this.extraBytes = extraBytes;
        initialize();
    }

    public InstDescription(String mnemonic, String expr, int extraBytes, Inst inst) {
        this.mnemonic = mnemonic;
        this.inst = inst;
        this.cb = false;
        this.expr = expr;
        this.extraBytes = extraBytes;
        initialize();
    }

    private void initialize() {

        //Calculate the regex pattern
        String regex = "^" + getCanonicalExpr().replaceAll("x", "[0-1]") + "$";
        pattern = Pattern.compile(regex);

        //Calculate number of placeholders
        numberOfPlaceholders = (int) getCanonicalExpr().chars().filter(ch -> ch == 'x').count();

    }

    public boolean matches(int opcode) {
        if (!cb) {
            return true;
        } else {
            return false;
        }
    }

    public boolean matchesCB(int opcode) {
        if (cb) {
            return false;
        } else {
            return false;
        }
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public Inst getInst() {
        return inst;
    }

    public int getExtraBytes() {
        return extraBytes;
    }

    public String getExpr() {
        return expr;
    }

    public boolean isCb() {
        return cb;
    }

    public int getNumberOfPlaceholders() {
        return numberOfPlaceholders;
    }

    public Pattern getPattern() {
        return pattern;
    }


    private String getCanonicalExpr() {
        return getExpr()
                .replaceAll(" ", "")
                .replaceAll("_", "")
                .replaceAll("X", "x");
    }
}
