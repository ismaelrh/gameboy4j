package com.ismaelrh.gameboy.cpu.cartridge;

import java.util.Map;

public class CartridgeConstants {
    protected static final Map<Byte, String> cartridgeTypesMap = Map.ofEntries(
            Map.entry((byte) 0x00, "ROM ONLY"),
            Map.entry((byte) 0x01, "MBC1"),
            Map.entry((byte) 0x02, "MBC1+RAM"),
            Map.entry((byte) 0x03, "MBC1+RAM+BATTERY"),
            Map.entry((byte) 0x05, "MBC2"),
            Map.entry((byte) 0x06, "MBC2+BATTERY"),
            Map.entry((byte) 0x08, "ROM+RAM"),
            Map.entry((byte) 0x09, "ROM+RAM+BATTERY"),
            Map.entry((byte) 0x0B, "MMM01"),
            Map.entry((byte) 0x0C, "MMM01+RAM"),
            Map.entry((byte) 0x0D, "MMM01+RAM+BATTERY"),
            Map.entry((byte) 0x0F, "MBC3+TIMER+BATTERY"),
            Map.entry((byte) 0x10, "MBC3+TIMER+RAM`BATTERY"),
            Map.entry((byte) 0x11, "MBC3"),
            Map.entry((byte) 0x12, "MBC3+RAM"),
            Map.entry((byte) 0x13, "MBC3+RAM+BATTERY"),
            Map.entry((byte) 0x19, "MBC5"),
            Map.entry((byte) 0x1A, "MBC5+RAM"),
            Map.entry((byte) 0x1B, "MBC5+RAM+BATTERY"),
            Map.entry((byte) 0x1C, "MBC5+RUMBLE"),
            Map.entry((byte) 0x1D, "MBC5+RUMBLE+RAM"),
            Map.entry((byte) 0x1E, "MBC5+RUMBLE+RAM+BATTERY"),
            Map.entry((byte) 0x20, "MBC6"),
            Map.entry((byte) 0x22, "MBC7+SENSOR+RUMBLE+RAM+BATTERY"),
            Map.entry((byte) 0xFC, "POCKET CAMERA"),
            Map.entry((byte) 0xFD, "BANDAI TAMA5"),
            Map.entry((byte) 0xFE, "HuC3"),
            Map.entry((byte) 0xFF, "HuC1+RAM+BATTERY")
    );

    protected final static Map<Byte, Integer> romSizeBytesMap = Map.ofEntries(
            Map.entry((byte) 0x00, 32 * 1024),
            Map.entry((byte) 0x01, 64 * 1024),
            Map.entry((byte) 0x02, 128 * 1024),
            Map.entry((byte) 0x03, 256 * 1024),
            Map.entry((byte) 0x04, 512 * 1024),
            Map.entry((byte) 0x05, 1024 * 1024),
            Map.entry((byte) 0x06, 2 * 1024 * 1024),
            Map.entry((byte) 0x07, 4 * 1024 * 1024),
            Map.entry((byte) 0x08, 8 * 1024 * 1024),
            Map.entry((byte) 0x52, 1152 * 1024 * 1024),
            Map.entry((byte) 0x53, 1280 * 1024),
            Map.entry((byte) 0x54, 1536 * 1024)
    );

    protected final static Map<Byte, Integer> ramSizeBytesMap = Map.ofEntries(
            Map.entry((byte) 0x00, 0),
            Map.entry((byte) 0x01, 2 * 1024),
            Map.entry((byte) 0x02, 8 * 1024),
            Map.entry((byte) 0x03, 32 * 1024),
            Map.entry((byte) 0x04, 128 * 1024),
            Map.entry((byte) 0x05, 64 * 1024)
    );
}
