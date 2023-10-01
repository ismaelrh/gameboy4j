package com.ismaelrh.gameboy.cpu.cartridge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class MBC1Cartridge extends Cartridge {

    private static final Logger log = LogManager.getLogger(MBC1Cartridge.class);

    private static final int[] NINTENDO_LOGO = {
            0xCE, 0xED, 0x66, 0x66, 0xCC, 0x0D, 0x00, 0x0B, 0x03, 0x73, 0x00, 0x83, 0x00, 0x0C, 0x00, 0x0D,
            0x00, 0x08, 0x11, 0x1F, 0x88, 0x89, 0x00, 0x0E, 0xDC, 0xCC, 0x6E, 0xE6, 0xDD, 0xDD, 0xD9, 0x99,
            0xBB, 0xBB, 0x67, 0x63, 0x6E, 0x0E, 0xEC, 0xCC, 0xDD, 0xDC, 0x99, 0x9F, 0xBB, 0xB9, 0x33, 0x3E
    };

    private final static String CARTRIDGE_TYPE = "MBC1";
    private final static int MAX_CARTRIDGE_SIZE_BYTES = 2 * 1024 * 1024;


    private final static int EXTERNAL_RAM_SIZE_BYTES = 32 * 1024;
    private boolean ramEnabled = false;

    /*
     * If the cart is not large enough to use the 2-bit register
     * (<= 8 KiB RAM and <= 512 KiB ROM) this mode select has no observable effect.
     */

    private byte romBankLow = (char) 0x00;

    private byte ramBankRomUpper = (char) 0x00;

    private final static byte BANK_MODE_ROM = (byte) 0x00;
    private final static byte BANK_MODE_RAM = (byte) 0x01;

    private char bankMode = BANK_MODE_ROM;

    private byte[] externalRam;

    private boolean multicart;

    public MBC1Cartridge(byte[] content) throws Exception {
        super(CARTRIDGE_TYPE, MAX_CARTRIDGE_SIZE_BYTES, content);
        this.externalRam = new byte[EXTERNAL_RAM_SIZE_BYTES];
        this.multicart = isMulticart();
    }

    public MBC1Cartridge(String path) throws Exception {
        super(CARTRIDGE_TYPE, MAX_CARTRIDGE_SIZE_BYTES, path);
        this.externalRam = new byte[EXTERNAL_RAM_SIZE_BYTES];
        this.multicart = isMulticart();
    }


    protected void setRam(byte[] ram) throws Exception {
        if (ram.length > EXTERNAL_RAM_SIZE_BYTES) {
            throw new Exception("Given RAM cannot be more than " + EXTERNAL_RAM_SIZE_BYTES + " bytes");
        }
        externalRam = ram;
    }


    @Override
    public byte read(char address) {

        //0000-3FFF: Rom Bank 00 (First 16KBytes of ROM)
        if (inRange(address, 0x0000, 0x3FFF)) {
            int bankStartAddress = ROM_BANK_SIZE_BYTES * getRomBankForBaseBank();
            return rawData[bankStartAddress + address];
        }

        //4000-7FFF: Rom Bank 01-7F (Switchable 16KBytes of ROM)
        if (inRange(address, 0x4000, 0x7FFF)) {
            if (romBankLow == 0x04) {
                int a = 3;
            }
            int relativeAddress = (address - 0x4000) & 0xFFFF;
            int bankStartAddress = ROM_BANK_SIZE_BYTES * getRomBankForSwitchableBanks();

            return rawData[bankStartAddress + relativeAddress];
        }

        //A000-BFFF: RAM Bank 00-03, if any (Switchable 8KBytes of RAM)
        if (inRange(address, 0xA000, 0xBFFF)) {
            if (ramEnabled) {
                int relativeAddress = (address - 0xA000) & 0xFFFF;
                int bankStartAddress = RAM_BANK_SIZE_BYTES * getRamBank();
                return externalRam[bankStartAddress + relativeAddress];
            }
            return (byte) 0xFF; //Cannot read, returns open bus value (mostly 0xFF, not guaranteed)
        }

        String addressString = String.format("0x%04X", (short) address);
        log.error("Attempted to read from write-only cartridge space! Address " + addressString);
        return 0x0000; //Cannot read
    }

    private boolean inRange(char address, int start, int end) {
        return address >= (char) start && address <= (char) end;
    }

    @Override
    public void write(char address, byte data) {
        //Write into RAM Bank
        if (inRange(address, 0xA000, 0xBFFF)) {
            if (ramEnabled) {
                int relativeAddress = (address - 0xA000) & 0xFFFF;
                int bankStartAddress = RAM_BANK_SIZE_BYTES * getRamBank();
                externalRam[bankStartAddress + relativeAddress] = data;
            }
        }
        //RAM Enable (any value with 0A in the lower 4 bits enables RAM)
        else if (inRange(address, 0x0000, 0x1FFF)) {
            ramEnabled = (data & 0x0F) == 0x0A;
        }
        //ROM Bank Number (lower 5 bits of the ROM Bank Number)
        else if (inRange(address, 0x2000, 0x3FFF)) {
            romBankLow = (byte) (data & 0x1F);
            //printBankInfo();
        }
        //RAM Bank Number - or - Upper Bits of ROM Bank Number
        else if (inRange(address, 0x4000, 0x5FFF)) {
            ramBankRomUpper = (byte) (data & 0x03); //Two lower bits
            //printBankInfo();
        }
        //ROM/RAM Mode Select
        else if (inRange(address, 0x6000, 0x7FFF)) {
            bankMode = (char) (data & 0x01);
            //printBankInfo();
        } else {
            String addressString = String.format("0x%04X", (short) address);
            String dataString = String.format("0x%02X", data);
            log.error("Attempted to write to read only cartridge address! Address " + addressString + ", Data " + dataString + " Ignored.");
        }

    }

    private String f(byte data) {
        return String.format("%02X", (int) data);
    }


    private String f(char data) {
        return String.format("%04X", (int) data);
    }

    private void printBankInfo() {
        System.out.println("Up: " + f(ramBankRomUpper) + " Low:" + f(romBankLow) + " Mode: " + f(bankMode));
    }

    //Upper 2 bits of bank only used if RAM is big enough (> 8 KiB)
    private boolean ramNeeds2upperBits() {
        return getRamSizeBytes() > 8 * 1024;
    }

    private byte getRomBankForBaseBank() {

        if (bankMode == BANK_MODE_RAM) {
            byte result = (byte) (((ramBankRomUpper & 0x03) << 5) & 0xFF);
            if(multicart){
                result >>= 1;
            }
            return (byte) ((result % getRomBanks()) & 0xFF);
        } else {
            return 0x00;
        }
    }

    private byte getRomBankForSwitchableBanks() {
        byte result = romBankLow;

        if (getRomBanks() > 32) {
            result = (byte) (result & 0x1F);
            //Clear upper bits
            result = (byte) (result & 0x1F);
            //Set upper bits
            result = (byte) (result | (byte) ((ramBankRomUpper & 0x03) << 5));
        }

        result = correctRomBank(result); //Banks 0x00, 0x20, 0x40 and 0x60  are added 1

        if(multicart){
            result = (byte) (((result >> 1) & 0x30) | (result & 0x0f));
        }

        return (byte) ((result % getRomBanks()) & 0xFF);
    }

    private byte getRamBank() {
        byte result = ramBankRomUpper;
        if (bankMode == BANK_MODE_RAM && getRamBanks() > 1) {
            return (byte) ((result % getRamBanks()) & 0xFF);
        }
        return 0x00;    //RAM bank disabled, uses 0.
    }

    private byte correctRomBank(byte romBank) {
        if (romBank == 0x00 || romBank == 0x20 || romBank == 0x40 || romBank == 0x60) {
            return (byte) (romBank + 1);
        }
        return romBank;
    }

    private boolean isMulticart() {
        int logoCount = 0;
        for (int i = 0; i < rawData.length; i += 0x4000) {
            if(hasNintendoLogo(i)){
                logoCount+=1;
            }
        }
        return getRomBanks() == 64 && logoCount > 1;
    }

    private boolean hasNintendoLogo(int sectionStart) {
        for (int i = 0; i < NINTENDO_LOGO.length; i++) {
            if (rawData[sectionStart + i + 0x104] != (byte) (NINTENDO_LOGO[i] & 0xFF)) {
                return false;
            }
        }
        return true;
    }

    //TODO: last comment
    //https://www.reddit.com/r/EmuDev/comments/dsa875/emulating_gb_memory_controllers/
    //apply 2 bits to the largest one
}
