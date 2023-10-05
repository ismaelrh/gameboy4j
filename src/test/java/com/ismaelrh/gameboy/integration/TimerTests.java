package com.ismaelrh.gameboy.integration;

import org.junit.Test;

public class TimerTests extends IntegrationTest{

    @Test
    public void doDivWrite() throws Exception {
        check("div_write.gb",10_000_000,"P0EF23MszeO3vwUIaaXjpg==");
    }

    @Test
    public void doTim00() throws Exception {
        check("tim00.gb",10_000_000,"igHALfPAMYIkBXMbgZ3f1Q==");
    }

    @Test
    public void doTim00DivTrigger() throws Exception {
        check("tim00_div_trigger.gb",10_000_000,"igHALfPAMYIkBXMbgZ3f1Q==");
    }

    @Test
    public void doTim01() throws Exception {
        check("tim01.gb",10_000_000,"ZOnSCd1uHRoxaYs0b0K4Jw==");
    }

    @Test
    public void doTim01DivTrigger() throws Exception {
        check("tim01_div_trigger.gb",10_000_000,"f6SuJuwQoKfpEpF8UI5yxw==");
    }

    @Test
    public void doTim10() throws Exception {
        check("tim10.gb",10_000_000,"igHALfPAMYIkBXMbgZ3f1Q==");
    }

    @Test
    public void doTim10DivTrigger() throws Exception {
        check("tim10_div_trigger.gb",10_000_000,"pdZ2ri3r4I3FhuebWvIZKg==");
    }

    @Test
    public void doTim11() throws Exception {
        check("tim11.gb",10_000_000,"igHALfPAMYIkBXMbgZ3f1Q==");
    }

    @Test
    public void doTim11DivTrigger() throws Exception {
        check("tim11_div_trigger.gb",10_000_000,"igHALfPAMYIkBXMbgZ3f1Q==");
    }

    @Test
    public void doTimaReload() throws Exception {
        check("tima_reload.gb",10_000_000,"ByRBQBpNAyGTivHoVkKgvQ==");
    }

    private void check(String specificFile, long cycles, String expectedHash) throws Exception{
        checkLcdHash("mooneye/acceptance/timer/" + specificFile,cycles,expectedHash);
    }

}
