package com.ismaelrh.gameboy.cpu.cartridge;

class BasicCartridge extends Cartridge {


    //Cartridge: 0x0000 to 0x7FFF (32KB)
    private final static String CARTRIDGE_TYPE = "BASIC";
    private final static int CARTRIDGE_SIZE_BYTES = 32 * 1024;

    public BasicCartridge(String filePath) throws Exception {
        super(CARTRIDGE_TYPE, CARTRIDGE_SIZE_BYTES, filePath);
    }

    public BasicCartridge(byte[] data) throws Exception {
        super(CARTRIDGE_TYPE, CARTRIDGE_SIZE_BYTES, data);
    }

    @Override
    public byte read(char address) {
        return rawData[address];
    }

    @Override
    public void write(char address, byte data) {
        //Write is disabled on this cartridges
        String addressString = String.format("0x%04X", (short) address);
        String dataString = String.format("0x%02X", data);
        log.error("Attempted to write to read only cartridge! Address " + addressString + ", Data " + dataString + " Ignored.");
    }
}
