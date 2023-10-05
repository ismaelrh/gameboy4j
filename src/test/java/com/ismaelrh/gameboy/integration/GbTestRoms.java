package com.ismaelrh.gameboy.integration;

import org.junit.Test;

public class GbTestRoms extends IntegrationTest{

    @Test
    public void doCpuInstrs() throws Exception {
        checkLcdHash("gb-test-roms/cpu_instrs.gb",250_000_000,"WHWjaqV8znTxoTmzwvwYww==");
    }

    @Test
    public void doInstrTiming() throws Exception {
        checkLcdHash("gb-test-roms/instr_timing.gb",5_000_000,"PIgWdLeQFJcH2mnHuOHaaQ==");
    }

}
