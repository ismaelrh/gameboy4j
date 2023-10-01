package com.ismaelrh.gameboy.integration;

import com.ismaelrh.gameboy.GameBoy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DmgAcid2Test extends IntegrationTest{

    @Test
    public void doDmgAcid2() throws Exception {
        checkLcdHash("acid/dmg-acid2.gb",1_000_000,"ZpbGoU5sCHw8glIx9aDhGA==");
    }

}
