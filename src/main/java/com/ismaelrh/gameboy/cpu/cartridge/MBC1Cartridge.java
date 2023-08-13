package com.ismaelrh.gameboy.cpu.cartridge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

public class MBC1Cartridge implements Cartridge {

    private static final Logger log = LogManager.getLogger(MBC1Cartridge.class);

    private final static int MAX_CARTRIDGE_SIZE_BYTES = 2 * 1024 * 1024;
    private final static int ROM_BANK_SIZE_BYTES = 16384;
    private final static int RAM_BANK_SIZE_BYTES = 8 * 1024;

    private final static int EXTERNAL_RAM_SIZE_BYTES = 32 * 1024;
    private boolean ramEnabled = false;


    private byte romBankLow = (char) 0x00;

    private byte ramBankRomUpper = (char) 0x00;

    private final static byte BANK_MODE_ROM = (byte) 0x00;
    private final static byte BANK_MODE_RAM = (byte) 0x01;

    private char bankMode = BANK_MODE_ROM;


    //Contains full data of cartridge, including banks
    private final byte[] fullCartridge;

    private byte[] externalRam;

    public MBC1Cartridge(String name, byte[] content) throws Exception {
        fullCartridge = new byte[MAX_CARTRIDGE_SIZE_BYTES];
        externalRam = new byte[EXTERNAL_RAM_SIZE_BYTES];
        int realSize = loadContent(content);
        log.info("Loaded cartridge MBC1'" + name + "' with size " + realSize + " bytes");
    }

    public MBC1Cartridge(String name, String filePath) throws Exception {
        fullCartridge = new byte[MAX_CARTRIDGE_SIZE_BYTES];
        externalRam = new byte[EXTERNAL_RAM_SIZE_BYTES];
        byte[] content = readFile(filePath);
        int realSize = loadContent(content);
        log.info("Loaded cartridge MBC1'" + name + "' with size " + realSize + " bytes");
    }

    protected void setRam(byte[] ram) throws Exception {
        if(ram.length > EXTERNAL_RAM_SIZE_BYTES){
            throw new Exception("Given RAM cannot be more than " + EXTERNAL_RAM_SIZE_BYTES + " bytes");
        }
        externalRam = ram;
    }
    private byte[] readFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.isFile() || !file.canRead()) {
            throw new Exception("Cannot read file " + filePath);
        }
        byte[] content = Files.readAllBytes(file.toPath());
        if (content.length > MAX_CARTRIDGE_SIZE_BYTES) {
            throw new Exception("Read file cannot fit into cartridge! Max is 2MB, file is " + content.length + "B");
        }
        return content;
    }

    private int loadContent(byte[] content) throws Exception {
        if (content.length > MAX_CARTRIDGE_SIZE_BYTES) {
            throw new Exception("Read file cannot fit into cartridge! Max is 2MB, file is " + content.length + "B");
        }
        for (int i = 0; i < content.length; i++) {
            fullCartridge[i] = content[i];
        }
        return content.length;
    }


    @Override
    public byte read(char address) {

        //0000-3FFF: Rom Bank 00 (First 16KBytes of ROM)
        if (inRange(address, 0x0000, 0x3FFF)) {
            return fullCartridge[address];
        }

        //4000-7FFF: Rom Bank 01-7F (Switchable 16KBytes of ROM)
        if (inRange(address, 0x4000, 0x7FFF)) {
            int relativeAddress = (address - 0x4000) & 0xFFFF;
            int bankStartAddress = ROM_BANK_SIZE_BYTES * getRomBank();
            return fullCartridge[bankStartAddress + relativeAddress];
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
                int relativeAddress =  (address - 0xA000) & 0xFFFF;
                int bankStartAddress =  RAM_BANK_SIZE_BYTES * getRamBank();
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
        }
        //RAM Bank Number - or - Upper Bits of ROM Bank Number
        else if (inRange(address, 0x4000, 0x5FFF)) {
            ramBankRomUpper = (byte) (data & 0x03); //Two lower bits
        }
        //ROM/RAM Mode Select
        else if (inRange(address, 0x6000, 0x7FFF)) {
            bankMode = (char) (data & 0x01);
        } else {
            String addressString = String.format("0x%04X", (short) address);
            String dataString = String.format("0x%02X", data);
            log.error("Attempted to write to read only cartridge address! Address " + addressString + ", Data " + dataString + " Ignored.");
        }

    }

    private byte getRomBank() {
        byte result = romBankLow;
        if (bankMode == BANK_MODE_ROM) {
            result = (byte) (result & 0x1F);
            //Clear upper bits
            result = (byte) (result & 0x1F);
            //Set upper bits
            result = (byte) (result | (byte) ((ramBankRomUpper & 0x03) << 5));
        }
        return correctRomBank(result);
    }

    private byte getRamBank() {
        byte result = ramBankRomUpper;
        if (bankMode == BANK_MODE_RAM) {
            return result;
        }
        return 0x00;    //RAM bank disabled, uses 0.
    }

    private byte correctRomBank(byte romBank) {
        if (romBank == 0x00 || romBank == 0x20 || romBank == 0x40 || romBank == 0x60) {
            return (byte) (romBank + 1);
        }
        return romBank;
    }

    //TODO: last comment
    //https://www.reddit.com/r/EmuDev/comments/dsa875/emulating_gb_memory_controllers/
    //apply 2 bits to the largest one
}
