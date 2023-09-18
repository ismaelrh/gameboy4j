package com.ismaelrh.gameboy.cpu.cartridge;

import org.junit.Before;
import org.junit.*;

import static org.junit.Assert.*;

public class MBC1CartridgeTest {

    private MBC1Cartridge cartridge;

    @Before
    public void setUp() throws Exception {
        byte[] romContent = generateRom();
        byte[] ramContent = generateRam();

        cartridge = new MBC1Cartridge(romContent);
        cartridge.setRam(ramContent);
    }

    private byte[] generateRom() throws Exception {
        byte[] romContent = new byte[2 * 1024 * 1024]; //2MBytes

        //0000-3FFF - ROM Bank 00
        fill(0x000, 0x3FFF, 0x00, romContent);

        //Fill the rest of the banks with the number of bank
        for (int i = 0x01; i <= 0x7F; i++) {
            int startAddr = 0x4000 * i;
            int endAddr = 0x4000 * (i + 1) - 1;
            fill(startAddr, endAddr, i, romContent);
        }

        return romContent;
    }

    private byte[] generateRam() {
        byte[] ramContent = new byte[32 * 1024]; //32 Kbytes

        for (int i = 0; i < 4; i++) {
            int startAddr =  0x2000 * i;
            int endAddr = 0x2000 * (i + 1) - 1;
            fill(startAddr, endAddr, i, ramContent);
        }

        return ramContent;
    }

    @Test
    public void readRomBank00() {
        for (char a = 0x00; a <= (char) 0x3FFF; a++) {
            assertEquals(0x00, cartridge.read(a));
        }
    }

    @Test
    public void readDefaultRomBank01() {
        for (char a = 0x4000; a <= (char) 0x7FFF; a++) {
            assertEquals(0x01, cartridge.read(a));
        }
    }

    @Test
    public void readRomBanks01_1F_ROMMode() {
        //Select ROM Mode
        cartridge.write((char) 0x6000, (byte) 0x00);
        for (byte bank = 0x01; bank <= 0x1F; bank++) {
            //Select ROM Bank <bank>
            cartridge.write((char) 0x2000, bank);
            for (char a = 0x4000; a <= (char) 0x7FFF; a++) {
                assertEquals(bank, cartridge.read(a));
            }
        }
    }

    @Test
    public void readRomBanks20_7F_ROMMode() {

        //Select ROM Mode
        cartridge.write((char) 0x6000, (byte) 0x00);

        for (int bank = 0x20; bank <= 0x7F; bank++) {
            if (bank != 0x20 && bank != 0x40 && bank != 0x60) { //Not accessible
                byte lowFiveBits = (byte) (bank & 0x1F);
                byte highTwoBits = (byte) ((bank & 0x60) >> 5); //Keep bits 6&7, and put them at positions 1&2.
                //Select ROM Bank <bank>
                cartridge.write((char) 0x2000, lowFiveBits);
                //Select Upper ROM Bank bits
                cartridge.write((char) 0x5000, highTwoBits);
                for (char a = 0x4000; a <= (char) 0x7FFF; a++) {
                    assertEquals(bank, cartridge.read(a));
                }
            }
        }
    }



    //TODO:what happens if try to access high rom bank but not accessible??


    @Test
    public void readRomBanks00_1F_RAMMode() {
        //Select RAM Mode
        cartridge.write((char) 0x6000, (byte) 0x01);
        for (byte bank = 0x01; bank <= 0x1F; bank++) {
            //Select ROM Bank <bank>
            cartridge.write((char) 0x2000, bank);
            for (char a = 0x4000; a <= (char) 0x7FFF; a++) {
                assertEquals(bank, cartridge.read(a));
            }
        }
    }

    @Test
    public void readRam_disabled_reads_zero() {
        for (char addr = 0xA000; addr <= 0xBFFF; addr++) {
            assertEquals((byte)0xFF, cartridge.read(addr));
        }
    }

    @Test
    public void writeRam_disabled_noEffect() {
        for (char addr = 0xA000; addr <= 0xBFFF; addr++) {
            cartridge.write(addr, (byte) 0xBE);
            assertEquals((byte)0xFF, cartridge.read(addr));
        }
    }

    @Test
    public void ramEnabledOK() {
        //Enable RAM, check that writes work
        cartridge.write((char) 0x0000, (byte) 0x0A);
        for (char addr = 0xA000; addr <= 0xBFFF; addr++) {
            cartridge.write(addr, (byte) 0xBE);
            assertEquals((byte) 0xBE, cartridge.read(addr));
        }
        //Disable, now all reads will be FF
        cartridge.write((char) 0x0000, (byte) 0x00);
        for (char addr = 0xA000; addr <= 0xBFFF; addr++) {
            assertEquals((byte) 0xFF, cartridge.read(addr));
        }
    }

    //TODO: what happens with higher ram bank if mode rom is selected

    @Test
    public void readAllRamBanks_RamMode(){
        //Enable RAM
        cartridge.write((char) 0x0000, (byte) 0x0A);
        //RAM mode
        cartridge.write((char) 0x6000, (byte) 0x01);
        for(int bank = 0; bank < 4; bank++){
            //Select Bank
            cartridge.write((char) 0x4000, (byte) bank);
            for (char a = 0xA000; a <= (char) 0xBFFF; a++) {
                assertEquals(bank, cartridge.read(a));
            }
        }
    }

    private void fill(int start, int end, int value, byte[] content) {
        for (int i = start; i <= end; i++) {  //int bc it's not addressable with 2 bytes!
            content[i] = (byte) (value & 0xFF);
        }
    }


}
