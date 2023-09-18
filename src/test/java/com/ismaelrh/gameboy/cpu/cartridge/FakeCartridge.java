package com.ismaelrh.gameboy.cpu.cartridge;

public class FakeCartridge extends Cartridge {

    //Cartridge: 0x0000 to 0x7FFF (32KB)
    private final static int CARTRIDGE_SIZE_BYTES = 32768;
    private byte[] cartridge = new byte[CARTRIDGE_SIZE_BYTES];

    public FakeCartridge() throws Exception{
        super("FAKE", CARTRIDGE_SIZE_BYTES, new byte[]{});
    }

    @Override
    public byte read(char address) {
        return cartridge[address];
    }

    @Override
    public void write(char address, byte data) {
        cartridge[address] = data;
    }


}
