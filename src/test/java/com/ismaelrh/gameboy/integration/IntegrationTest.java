package com.ismaelrh.gameboy.integration;

import com.ismaelrh.gameboy.GameBoy;
import com.ismaelrh.gameboy.GameBoyOptions;
import com.ismaelrh.gameboy.TestLcd;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    protected GameBoy executeRom(String romPath, long cycles) throws Exception {
        GameBoy gameBoy = new GameBoy(new TestLcd());
        gameBoy.loadCartridge(new File("src/test/resources", romPath).getAbsolutePath());
        gameBoy.run(new GameBoyOptions(cycles,-1));
        return gameBoy;
    }

    protected void checkLcdHash(String romPath, long cycles, String expectedHash) throws Exception{
        GameBoy result = executeRom(romPath,cycles);
        assertEquals(expectedHash,result.getLcd().getHash());
    }
}
