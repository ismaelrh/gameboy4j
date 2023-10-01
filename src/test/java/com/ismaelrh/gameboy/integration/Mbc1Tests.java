package com.ismaelrh.gameboy.integration;

import org.junit.Test;

public class Mbc1Tests extends IntegrationTest{

    @Test
    public void doBitsBank1() throws Exception {
        check("bits_bank1.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doBitsBank2() throws Exception {
        check("bits_bank2.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doBitsMode() throws Exception {
        check("bits_mode.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doBitsRamg() throws Exception {
        check("bits_ramg.gb",30_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doRam64Kb() throws Exception {
        check("ram_64kb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doRam256Kb() throws Exception {
        check("ram_256kb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doRom512kb() throws Exception {
        check("rom_4Mb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }
    @Test
    public void doRom1Mb() throws Exception {
        check("rom_1Mb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doRom2Mb() throws Exception {
        check("rom_2Mb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doRom4Mb() throws Exception {
        check("rom_4Mb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doRom8Mb() throws Exception {
        check("rom_8Mb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doRom16Mb() throws Exception {
        check("rom_16Mb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doMulticart() throws Exception {
        check("multicart_rom_8Mb.gb",20_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }
    private void check(String specificFile, long cycles, String expectedHash) throws Exception{
        checkLcdHash("mooneye/emulator-only/mbc1/" + specificFile,cycles,expectedHash);
    }

}
