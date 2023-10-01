package com.ismaelrh.gameboy.integration;

import org.junit.Test;

public class GbTestRoms extends IntegrationTest{

    @Test
    public void doDmgAcid2() throws Exception {
        checkLcdHash("gb-test-roms/cpu_instrs/cpu_instrs.gb",250_000_000,"WHWjaqV8znTxoTmzwvwYww==");
    }

}
