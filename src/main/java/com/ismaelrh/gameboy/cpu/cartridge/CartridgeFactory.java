package com.ismaelrh.gameboy.cpu.cartridge;

public class CartridgeFactory {

    public static Cartridge create(String filePath) throws Exception {
        return create(Cartridge.readFile(filePath));
    }

    public static Cartridge create(byte[] data) throws Exception {
        return createCartridgeWithType(getCartridgeType(data), data);
    }

    private static byte getCartridgeType(byte[] data) {
        return data[0x147];
    }

    private static Cartridge createCartridgeWithType(byte cartridgeType, byte[] data) throws Exception {
        switch (cartridgeType) {
            case 0x00:
                return new BasicCartridge(data);
            case 0x01:
            case 0x02:
            case 0x03:
                return new MBC1Cartridge(data);
            default:
                throw new Exception("Cartridge with type " + cartridgeType + " is not implemented");
        }
    }

}
