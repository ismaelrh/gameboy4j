package com.ismaelrh.gameboy.debug.debugger.console;

import com.ismaelrh.gameboy.cpu.Registers;
import com.jakewharton.fliptables.FlipTable;

public class RegisterStatus {

    private final String[] headers = {"PC", "SP", "A", "B", "C", "D", "E", "HL"};

    private final Registers r;

    public RegisterStatus(Registers r) {
        this.r = r;
    }

    public void print() {
        String[][] data = {
                {f(r.getPC()), f(r.getSP()), f(r.getA()), f(r.getB()), f(r.getC()), f(r.getD()), f(r.getE()), f(r.getHL())}
        };
        System.out.println(FlipTable.of(headers, data));
    }

    private String f(char data) {
        return String.format("%04X", (int) data);
    }

    private String f(byte data) {
        return String.format("%02X", data);
    }
}
