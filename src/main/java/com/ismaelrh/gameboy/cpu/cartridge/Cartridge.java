package com.ismaelrh.gameboy.cpu.cartridge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import static com.ismaelrh.gameboy.cpu.cartridge.CartridgeConstants.*;

public abstract class Cartridge {

    protected final static int ROM_BANK_SIZE_BYTES = 16 * 1024;
    protected final static int RAM_BANK_SIZE_BYTES = 8 * 1024;

    protected static final Logger log = LogManager.getLogger(Cartridge.class);

    private String title;

    private String cartridgeType;

    private int romSizeBytes;

    private int ramSizeBytes;

    private final int MAX_CARTRIDGE_SIZE_BYTES;

    protected final String savePath;

    protected final byte[] rawData;


    protected Cartridge(String cartridgeType, int maxCartridgeSizeBytes, String path, String savePath) throws Exception {
        this(cartridgeType, maxCartridgeSizeBytes, Cartridge.readFile(path), savePath);
    }

    protected Cartridge(String cartridgeType, int maxCartridgeSizeBytes, byte[] data, String savePath) throws Exception {
        MAX_CARTRIDGE_SIZE_BYTES = maxCartridgeSizeBytes;
        rawData = new byte[MAX_CARTRIDGE_SIZE_BYTES];
        this.savePath = savePath;
        int realSize = loadContent(data);
        populateMetadata(data);
        if (realSize > MAX_CARTRIDGE_SIZE_BYTES) {
            throw new Exception("Read file cannot fit into cartridge! Max is " + MAX_CARTRIDGE_SIZE_BYTES + " B, file is " + realSize + "B");
        }
        log.info("Loaded cartridge " + cartridgeType + " with size " + realSize + " bytes");
    }

    private int loadContent(byte[] content) throws Exception {
        if (content.length > MAX_CARTRIDGE_SIZE_BYTES) {
            throw new Exception("Read file cannot fit into cartridge! Max is 2MB, file is " + content.length + "B");
        }
        for (int i = 0; i < content.length; i++) {
            rawData[i] = content[i];
        }
        return content.length;
    }

    private void populateMetadata(byte[] content) {
        try {
            this.title = new String(Arrays.copyOfRange(content, 0x134, 0x144), StandardCharsets.UTF_8).replaceAll("\0", "");
            this.cartridgeType = cartridgeTypesMap.getOrDefault(content[0x147], "UNKNOWN CARTRIDGE TYPE");
            this.romSizeBytes = romSizeBytesMap.getOrDefault(content[0x148], -1);
            this.ramSizeBytes = ramSizeBytesMap.getOrDefault(content[0x149], -1);
        } catch (Exception e) {

        }

    }

    public String getTitle() {
        return title;
    }

    public String getCartridgeType() {
        return cartridgeType;
    }

    public int getRomSizeBytes() {
        return romSizeBytes;
    }

    public int getRamSizeBytes() {
        return ramSizeBytes;
    }

    abstract public byte read(char address);

    abstract public void write(char address, byte data);

    static protected byte[] readFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.isFile() || !file.canRead()) {
            throw new Exception("Cannot read file " + filePath);
        }
        return Files.readAllBytes(file.toPath());
    }

    public int getRomBanks() {
        return romSizeBytes / ROM_BANK_SIZE_BYTES;
    }

    public int getRamBanks() {
        if (ramSizeBytes == ramSizeBytesMap.get((byte) 0x01) || ramSizeBytes == ramSizeBytesMap.get((byte) 0x02)) {
            return 1;
        }
        return ramSizeBytes / RAM_BANK_SIZE_BYTES;
    }

    protected void setRomSizeBytes(int size) {
        this.romSizeBytes = size;
    }

    protected void setRamSizeBytes(int size) {
        this.ramSizeBytes = size;
    }
}
