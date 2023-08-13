package com.ismaelrh.gameboy.cpu.cartridge;

import com.ismaelrh.gameboy.cpu.instructions.InstDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;

public class BasicCartridge implements Cartridge {

    private static final Logger log = LogManager.getLogger(BasicCartridge.class);

    private String name;
    private String filePath;

    //Cartridge: 0x0000 to 0x7FFF (32KB)
    private final static int CARTRIDGE_SIZE_BYTES = 32768;
    private byte[] cartridge;
    private int realSize;

    public BasicCartridge(String name, String filePath) throws Exception {
        this.name = name;
        this.filePath = filePath;
        cartridge = new byte[CARTRIDGE_SIZE_BYTES];
        loadFile();
        log.info("Loaded cartridge '" + name + "' with size " + realSize + " bytes");
    }

    private void loadFile() throws Exception {
        File file = new File(filePath);
        if (!file.isFile() || !file.canRead()) {
            throw new Exception("Cannot read file " + filePath);
        }
        byte[] content = Files.readAllBytes(file.toPath());
        if (content.length > 32768) {
            throw new Exception("Read file cannot fit into cartridge! Max is 32768B, file is " + content.length + "B");
        }
        realSize = content.length;
        for (int i = 0; i < content.length; i++) {
            cartridge[i] = content[i];
        }
    }
    @Override
    public byte read(char address) {
        return cartridge[address];
    }

    @Override
    public void write(char address, byte data) {
        //Write is disabled on this cartridges
        String addressString = String.format("0x%04X", (short) address);
        String dataString = String.format("0x%02X", data);
        log.error("Attempted to write to read only cartridge! Address " + addressString + ", Data " + dataString + " Ignored.");
    }
}
