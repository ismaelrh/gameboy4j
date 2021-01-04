package com.ismaelrh.gameboy.cpu.memory;


import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Memory {

    private List<MemoryInterceptor> interceptors = new ArrayList<>();

    //TO-DO: redirect external memory and I/O where it corresponds
    private static final Logger log = LogManager.getLogger(Memory.class);

    /**
     * 16-bit (2 Byte) bus, so a (char) is used, as it is unsigned. Short is NOT unsigned.
     * Allows to read/write 8-bit (1 Byte) at a time (byte)
     */

    //Cartridge: 0x0000 to 0x7FFF (32KB)
    private final static char CARTRIDGE_START = 0x0000;
    private Cartridge cartridge;

    //Video/tile RAM: 0x8000 to 0x9FFF (8KB)
    private final static char VIDEO_RAM_START = 0x8000;
    private final static int VIDEO_RAM_SIZE_BYTES = 8192;
    private byte[] videoRAM;

    //External Cartridge RAM: 0xA000 - 0xBFFF (8KB)
    private final static char EXTERNAL_RAM_START = 0xA000;
    private final static int EXTERNAL_RAM_SIZE_BYTES = 8192;
    private byte[] externalRAM;

    //Internal RAM: 0xC000 - 0xDFFF (8KB)
    private final static char INTERNAL_RAM_START = 0xC000;
    private final static int INTERNAL_RAM_SIZE_BYTES = 8192;
    private byte[] internalRAM;

    //Echo internal RAM: 0xE000 - 0xFDFF (7680 Bytes)
    private final static char ECHO_RAM_START = 0xE000;
    private final static int ECHO_RAM_SIZE_BYTES = 7680;

    //Sprite Attribute Memory (OAM): 0xFE00 - 0xFE9F (160 Bytes)
    private final static char SPRITE_RAM_START = 0xFE00;
    private final static int SPRITE_RAM_SIZE_BYTES = 160;
    private byte[] spriteRAM;

    //Unusable zone: 0xFEA0-FEFF
    private final static char UNUSABLE_RAM_START = 0xFEA0;
    private final static int UNUSABLE_RAM_SIZE_BYTES = 96;

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
        videoRAM = new byte[VIDEO_RAM_SIZE_BYTES];
        externalRAM = new byte[EXTERNAL_RAM_SIZE_BYTES];
        internalRAM = new byte[INTERNAL_RAM_SIZE_BYTES];
        spriteRAM = new byte[SPRITE_RAM_SIZE_BYTES];
        ioRAM = new byte[IO_RAM_SIZE_BYTES];
        highRAM = new byte[HIGH_RAM_SIZE_BYTES];
        interruptEnable = 0x0;    //TODO: Is this really initialized as 0x0? Check manual
        log.debug("Memory cleared");
    }

    public void addInterceptor(MemoryInterceptor interceptor) {
        this.interceptors.add(interceptor);
        interceptor.addMemory(this);
    }

    public byte read(char address) {

        byte result;
        if (address == INTERRUPT_ENABLE_START) {
            result = interruptEnable;
        } else if (address >= HIGH_RAM_START) {
            result = highRAM[address - HIGH_RAM_START];
        } else if (address >= IO_RAM_START) {
            result = ioRAM[address - IO_RAM_START];
        } else if (address >= UNUSABLE_RAM_START) {
            log.warn("Read unusable RAM @" + String.format("%04x", (int) address));
            result = 0x00; //In reality, returns garbage.
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
            if (cartridge != null) {
                result = cartridge.read(address);
            } else {
                log.error("Attempted to read from cartridge, but not inserted. Returned 0x00");
                result = 0x00;
            }
        }
        log.debug("Read [@" + String.format("%04x", (int) address) + "]=" + String.format("%02x", result));

        //Apply read interceptors
        for (MemoryInterceptor i : interceptors) {
            result = i.onRead(address, result);
        }
        return result;
    }

    public void write(char address, byte data) {

        //Apply write interceptors
        for (MemoryInterceptor i : interceptors) {
            data = i.onWrite(address, data);
        }

        if (address == INTERRUPT_ENABLE_START) {
            interruptEnable = data;
        } else if (address >= HIGH_RAM_START) {
            highRAM[address - HIGH_RAM_START] = data;
        } else if (address >= IO_RAM_START) {
            ioRAM[address - IO_RAM_START] = data;
        } else if (address >= UNUSABLE_RAM_START) {
            log.warn("Ignored writing into unusable RAM @" + String.format("%02x", (int) address));
            return;
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
            if (cartridge != null) {
                cartridge.write(address, data);
            } else {
                log.error("Attempted to write to cartridge, but it is not inserted");
            }
        }
        log.debug("Write [@" + String.format("%04x", (int) address) + "]=" + String.format("%02x", data));
    }

    public void insertCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    public void removeCartridge() {
        this.cartridge = null;
    }


    protected byte[] getVideoRAM() {
        return videoRAM;
    }

    protected byte[] getExternalRAM() {
        return externalRAM;
    }

    protected byte[] getInternalRAM() {
        return internalRAM;
    }

    protected byte[] getSpriteRAM() {
        return spriteRAM;
    }

    protected byte[] getIoRAM() {
        return ioRAM;
    }

    protected byte[] getHighRAM() {
        return highRAM;
    }

    protected byte getInterruptEnable() {
        return interruptEnable;
    }
}
