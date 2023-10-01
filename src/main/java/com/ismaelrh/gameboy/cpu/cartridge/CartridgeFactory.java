package com.ismaelrh.gameboy.cpu.cartridge;

public class CartridgeFactory {

    public static Cartridge create(String filePath, String savePath) throws Exception {
        return create(Cartridge.readFile(filePath), savePath);
    }

    public static Cartridge create(byte[] data, String savePath) throws Exception {
        return createCartridgeWithType(getCartridgeType(data), data, savePath);
    }

    private static byte getCartridgeType(byte[] data) {
        return data[0x147];
    }

    private static Cartridge createCartridgeWithType(byte cartridgeType, byte[] data, String savePath) throws Exception {
        switch (cartridgeType) {
            case 0x00:
                return new BasicCartridge(data);
            case 0x01:
            case 0x02:
            case 0x03:
                return new MBC1Cartridge(data, savePath);
            case 0x0F:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
                return new MBC3Cartridge(data, savePath);
            default:
                throw new Exception("Cartridge with type " + cartridgeType + " is not implemented");
        }
    }

}
