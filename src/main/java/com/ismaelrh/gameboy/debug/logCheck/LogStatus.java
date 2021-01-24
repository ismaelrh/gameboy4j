package com.ismaelrh.gameboy.debug.logCheck;

import com.ismaelrh.gameboy.cpu.Registers;
import com.jakewharton.fliptables.FlipTable;

public class LogStatus {

    private final String[] headers = {" ", "A", "F", "BC", "DE", "HL", "SP", "PC", "Cycles"};

    private int cycle;
    private Registers registers;

    public LogStatus(int cycle, Registers registers) {
        this.cycle = cycle;
        this.registers = registers;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public Registers getRegisters() {
        return registers;
    }

    public void setRegisters(Registers registers) {
        this.registers = registers;
    }

    public boolean isOk(int cycle, Registers registers) {
        return this.cycle == cycle && this.registers.equals(registers);
    }

    public void printDiff(int otherCycles, Registers otherRegisters) {
        //1st, expected
        //2nd, got
        String[][] data = {
                {"Expected", f(registers.getA()), flags(registers), f(registers.getBC()), f(registers.getDE()), f(registers.getHL()), f(registers.getSP()), f(registers.getPC()), "" + cycle},
                {"Got", f(otherRegisters.getA()), flags(otherRegisters), f(otherRegisters.getBC()), f(otherRegisters.getDE()), f(otherRegisters.getHL()), f(otherRegisters.getSP()), f(otherRegisters.getPC()), "" + otherCycles}
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
