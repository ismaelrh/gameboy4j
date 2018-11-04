package com.ismaelrh.gameboy;

import org.apache.log4j.Logger;

public class Memory {

	//TO-DO: redirect external memory and I/O where it corresponds
	private Logger log = Logger.getLogger(Memory.class);

	/**
	 * 16-bit (2 Byte) bus, so a (byte) is used, as it is unsigned. Short is NOT unsigned.
	 * Allows to read/write 8-bit (1 Byte) at a time (byte)
	 */
	//Cartridge: 0x0000 to 0x7FFF (32KB)
	private final static char CARTRIDGE_START = 0x0000;
	private final static int CARTRIDGE_SIZE_BYTES = 32000;
	private byte[] cartridge;

	//Video/tile RAM: 0x8000 to 0x9FFF (8KB)
	private final static char VIDEO_RAM_START = 0x8000;
	private final static int VIDEO_RAM_SIZE_BYTES = 8000;
	private byte[] videoRAM;

	//External Cartridge RAM: 0xA000 - 0xBFFF (8KB)
	private final static char EXTERNAL_RAM_START = 0xA000;
	private final static int EXTERNAL_RAM_SIZE_BYTES = 8000;
	private byte[] externalRAM;

	//Internal RAM: 0xC000 - 0xDFFF (8KB)
	private final static char INTERNAL_RAM_START = 0xC000;
	private final static int INTERNAL_RAM_SIZE_BYTES = 8000;
	private byte[] internalRAM;

	//Echo internal RAM: 0xE000 - 0xFDFF (8KB)
	private final static char ECHO_RAM_START = 0xE000;
	private final static int ECHO_RAM_SIZE_BYTES = 8000;

	//Sprite Attribute Memory (OAM): 0xFE00 - 0xFE9F (160 Bytes)
	private final static char SPRITE_RAM_START = 0xFE00;
	private final static int SPRITE_RAM_SIZE_BYTES = 160;
	private byte[] spriteRAM;

	//I/O ports mapped RAM: 0xFF00 - 0xFF7F (128 Bytes)
	private final static char IO_RAM_START = 0xFF00;
	private final static int IO_RAM_SIZE_BYTES = 0xFF00;
	private byte[] ioRAM;

	//High-RAM: 0xFF80 - 0xFFFE (127 Bytes)
	private final static char HIGH_RAM_START = 0xFF80;
	private final static int HIGH_RAM_SIZE_BYTES = 127;
	private byte[] highRAM;

	//Interrupt Enable Register: 0xFFFF (1 Byte)
	private final static char INTERRUPT_ENABLE_START = 0xFFFF;
	private final static int INTERRUPT_ENABLE_SIZE_BYTES = 1;
	private byte interruptEnable;

	public Memory() {
		clear();
	}

	public void clear() {
		cartridge = new byte[CARTRIDGE_SIZE_BYTES];
		videoRAM = new byte[VIDEO_RAM_SIZE_BYTES];
		externalRAM = new byte[EXTERNAL_RAM_SIZE_BYTES];
		internalRAM = new byte[INTERNAL_RAM_SIZE_BYTES];
		spriteRAM = new byte[SPRITE_RAM_SIZE_BYTES];
		ioRAM = new byte[IO_RAM_SIZE_BYTES];
		highRAM = new byte[HIGH_RAM_SIZE_BYTES];
		interruptEnable = 0x0;
		log.debug("Memory cleared");
	}

	public byte read(char address) {
		byte result;
		if (address == INTERRUPT_ENABLE_START) {
			result = interruptEnable;
		} else if (address >= HIGH_RAM_START) {
			result = highRAM[address - HIGH_RAM_START];
		} else if (address >= IO_RAM_START) {
			result = ioRAM[address - IO_RAM_START];
		} else if (address >= SPRITE_RAM_START) {
			result = spriteRAM[address - SPRITE_RAM_START];
		} else if (address >= ECHO_RAM_START) {
			result = internalRAM[address - ECHO_RAM_START];
		} else if (address >= INTERNAL_RAM_START) {
			result = internalRAM[address - INTERNAL_RAM_START];
		} else if (address >= EXTERNAL_RAM_START) {
			result = externalRAM[address - EXTERNAL_RAM_START];
		} else if (address >= VIDEO_RAM_START) {
			result = videoRAM[address - VIDEO_RAM_START];
		} else { //Cartridge mapped memory
			result = cartridge[address];
		}
		log.debug("Read from @" + String.format("%04x", (int) address) + "=" + String.format("%04x", result));
		return result;
	}

	public void write(char address, byte data) {
		if (address == INTERRUPT_ENABLE_START) {
			interruptEnable = data;
		} else if (address >= HIGH_RAM_START) {
			highRAM[address - HIGH_RAM_START] = data;
		} else if (address >= IO_RAM_START) {
			ioRAM[address - IO_RAM_START] = data;
		} else if (address >= SPRITE_RAM_START) {
			spriteRAM[address - SPRITE_RAM_START] = data;
		} else if (address >= ECHO_RAM_START) {
			internalRAM[address - ECHO_RAM_START] = data;
		} else if (address >= INTERNAL_RAM_START) {
			internalRAM[address - INTERNAL_RAM_START] = data;
		} else if (address >= EXTERNAL_RAM_START) {
			externalRAM[address - EXTERNAL_RAM_START] = data;
		} else if (address >= VIDEO_RAM_START) {
			videoRAM[address - VIDEO_RAM_START] = data;
		} else { //Cartridge mapped memory
			cartridge[address] = data;
		}
		log.debug("Write to from @" + String.format("%04x", (int) address) + "=" + String.format("%04x", data));
	}
}
