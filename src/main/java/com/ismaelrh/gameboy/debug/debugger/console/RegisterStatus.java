package com.ismaelrh.gameboy.debug.debugger.console;

import com.ismaelrh.gameboy.cpu.ExecutionInfo;
import com.ismaelrh.gameboy.cpu.Registers;
import com.jakewharton.fliptables.FlipTable;

public class RegisterStatus {

    private final String[] headers = {"A", "F", "BC", "DE", "HL", "SP", "PC", "Cycles"};

    private final Registers r;
    private final ExecutionInfo executionInfo;

    public RegisterStatus(Registers r, ExecutionInfo executionInfo) {
        this.r = r;
        this.executionInfo = executionInfo;
    }

    public void print() {
        String[][] data = {
                {f(r.getA()), flags(r), f(r.getBC()), f(r.getDE()), f(r.getHL()), f(r.getSP()), f(r.getPC()), "" + executionInfo.getCycles()}
        };
        System.out.println(FlipTable.of(headers, data));
    }

    private String f(char data) {
        return String.format("%04X", (int) data);
    }

    private String f(byte data) {
        return String.format("%02X", data);
    }

    private String flags(Registers r) {
        String res = "";
        res += r.checkFlagZ() ? "Z" : "-";
        res += r.checkFlagN() ? "N" : "-";
        res += r.checkFlagH() ? "H" : "-";
        res += r.checkFlagC() ? "C" : "-";
        return res;
    }
}
