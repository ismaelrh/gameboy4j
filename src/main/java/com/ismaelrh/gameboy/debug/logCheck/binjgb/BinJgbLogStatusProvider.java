package com.ismaelrh.gameboy.debug.logCheck.binjgb;

import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.debug.logCheck.LogStatus;
import com.ismaelrh.gameboy.debug.logCheck.LogStatusProvider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BinJgbLogStatusProvider implements LogStatusProvider {

    private BufferedReader reader;
    private Pattern logPattern = Pattern.compile("A:([a-zA-Z0-9]+) F:([ZNHC\\-]+) BC:([a-zA-Z0-9]+) DE:([a-zA-Z0-9]+) HL:([a-zA-Z0-9]+) SP:([a-zA-Z0-9]+) PC:([a-zA-Z0-9]+) \\(cy: ([0-9]+)\\)");

    public BinJgbLogStatusProvider(String filename){
        init(filename);
    }

    private void init(String filename) {
        try {
            reader = new BufferedReader(new FileReader(filename));
            //Ignore first seven lines
            for (int i = 0; i < 7; i++) {
                reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public LogStatus next() {
        try {
            String line = reader.readLine();
            return parseLine(line);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //A:D8 F:---C BC:0110 DE:c0a5 HL:40a6 SP:fffe PC:0207 (cy: 5360) ppu:+0 |[00]0x0207: 12        ld [de],a
    private LogStatus parseLine(String line) {
        Matcher m = logPattern.matcher(line);
        if (m.find()) {
            Registers r = new Registers();
            setA(r, m.group(1));
            setFlags(r, m.group(2));
            setBC(r, m.group(3));
            setDE(r, m.group(4));
            setHL(r, m.group(5));
            setSP(r, m.group(6));
            setPC(r, m.group(7));
            int cycles = getCyclesCount(m.group(8));
            return new LogStatus(cycles, r);
        } else {
            return null;
        }
    }

    private void setA(Registers r, String text) {
        r.setA((byte) (Integer.parseInt(text, 16) & 0xFF));
    }

    private void setFlags(Registers r, String text) {
        r.clearFlags();
        if (text.contains("Z")) {
            r.setFlagZ();
        }
        if (text.contains("N")) {
            r.setFlagN();
        }
        if (text.contains("H")) {
            r.setFlagH();
        }
        if (text.contains("C")) {
            r.setFlagC();
        }
    }

    private void setBC(Registers r, String text) {
        r.setBC((char) (Integer.parseInt(text, 16) & 0xFFFF));
    }

    private void setDE(Registers r, String text) {
        r.setDE((char) (Integer.parseInt(text, 16) & 0xFFFF));
    }

    private void setHL(Registers r, String text) {
        r.setHL((char) (Integer.parseInt(text, 16) & 0xFFFF));
    }

    private void setSP(Registers r, String text) {
        r.setSP((char) (Integer.parseInt(text, 16) & 0xFFFF));
    }

    private void setPC(Registers r, String text) {
        r.setPC((char) (Integer.parseInt(text, 16) & 0xFFFF));
    }

    private int getCyclesCount(String text) {
        return Integer.parseInt(text);
    }


}
