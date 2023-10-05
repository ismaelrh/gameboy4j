package com.ismaelrh.gameboy.cpu.cartridge;

import com.ismaelrh.gameboy.cpu.cartridge.rtc.RtcRegisters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

class MBC3Cartridge extends Cartridge {

    private static final Logger log = LogManager.getLogger(MBC3Cartridge.class);

    private final static String CARTRIDGE_TYPE = "MBC3";

    private final static int MAX_CARTRIDGE_SIZE_BYTES = 8 * 1024 * 1024;
    private final static int EXTERNAL_RAM_SIZE_BYTES = 32 * 1024;
    private boolean ramWriteEnabled = false;
    private byte romBank = 0x01;

    private byte ramRtcRegisterSelect = 0x00;

    private byte[] externalRam;

    private RtcRegisters rtcRegisters;

    public MBC3Cartridge(byte[] content, String savePath) throws Exception {
        super(CARTRIDGE_TYPE, MAX_CARTRIDGE_SIZE_BYTES, content, savePath);
        initialize();
    }

    public MBC3Cartridge(String path, String savePath) throws Exception {
        super(CARTRIDGE_TYPE, MAX_CARTRIDGE_SIZE_BYTES, path, savePath);
        initialize();
    }

    private void initialize() throws Exception {
        this.externalRam = new byte[EXTERNAL_RAM_SIZE_BYTES];
        this.rtcRegisters = new RtcRegisters();
        readSaveFile();
    }

    @Override
    public byte read(char address) {

        //0000-3FFF: Rom Bank 00 (First 16KBytes of ROM)
        if (inRange(address, 0x0000, 0x3FFF)) {
            return rawData[address];
        }

        //4000-7FFF: Rom Bank 01-7F (Switchable 16KBytes of ROM)
        if (inRange(address, 0x4000, 0x7FFF)) {
            int relativeAddress = (address - 0x4000) & 0xFFFF;
            int bankStartAddress = ROM_BANK_SIZE_BYTES * (getRomBank() & 0xFF);
            return rawData[bankStartAddress + relativeAddress];
        }

        //A000-BFFF: RAM Bank 00-03, if any (Switchable 8KBytes of RAM)
        if (inRange(address, 0xA000, 0xBFFF)) {
            if (ramRtcRegisterSelect >= 0x00 && ramRtcRegisterSelect <= 0x03) {
                int relativeAddress = (address - 0xA000) & 0xFFFF;
                int bankStartAddress = RAM_BANK_SIZE_BYTES * getRamBank();
                return externalRam[bankStartAddress + relativeAddress];
            } else {
                return rtcRegisters.readRegister(ramRtcRegisterSelect);
            }
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
        if (inRange(address, 0xA000, 0xBFFF) && ramWriteEnabled) {
            if (ramRtcRegisterSelect >= 0x00 && ramRtcRegisterSelect <= 0x03) {
                int relativeAddress = (address - 0xA000) & 0xFFFF;
                int bankStartAddress = RAM_BANK_SIZE_BYTES * getRamBank();
                externalRam[bankStartAddress + relativeAddress] = data;
            } else {
                rtcRegisters.writeRegister(ramRtcRegisterSelect, data);
            }
        } //Ram and Timer Enable
        else if (inRange(address, 0x0000, 0x1FFF)) {
            boolean oldValue = ramWriteEnabled;
            if (data == 0x0A) {
                ramWriteEnabled = true;
            } else if (data == 0x00) {
                ramWriteEnabled = false;
            }

            if (oldValue && !ramWriteEnabled) {
                writeSaveFile();
            }
        }
        //ROM Bank Number
        else if (inRange(address, 0x2000, 0x3FFF)) {
            romBank = (byte) (data & 0x7F);
        }
        //RAM Bank Number or RTC Register Select
        else if (inRange(address, 0x4000, 0x5FFF)) {
            ramRtcRegisterSelect = data;
        }

        //Latch Clock Data (Write Only)
        else if (inRange(address, 0x6000, 0x7FFF)) {
            rtcRegisters.writeLatch(data);
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

    //Upper 2 bits of bank only used if RAM is big enough (> 8 KiB)
    private boolean ramNeeds2upperBits() {
        return getRamSizeBytes() > 8 * 1024;
    }


    private byte getRomBank() {
        byte result = romBank;
        if (result == 0x00) {
            result += 0x01;
        }
        return (byte) ((result % getRomBanks()) & 0xFF);
    }

    private byte getRamBank() {
        return ramRtcRegisterSelect;
    }

    private void readSaveFile() throws Exception {
        if (savePath != null && Files.exists(Path.of(savePath))) {
            byte[] saveFile = Cartridge.readFile(savePath);
            for (int i = 0; i < Math.min(saveFile.length, externalRam.length); i++) {
                externalRam[i] = saveFile[i];
            }
        }
    }

    private void writeSaveFile() {
        try {
            if (savePath != null) {
                Files.write(Path.of(savePath), externalRam);
            }
        } catch (Exception ex) {
            log.error("Error writing savefile to " + savePath, ex);
        }
    }

}
