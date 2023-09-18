package com.ismaelrh.gameboy.cpu.memory;


import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Memory {

    //For interruptions
    private final static char VBLANK_MASK = (char) 0x01; //0000-0001
    private final static char VBLANK_ISR = (char) 0x40;

    private final static char LCD_MASK = (char) 0x02; //0000-0010
    private final static char LCD_ISR = (char) 0x48;

    private final static char TIMER_MASK = (char) 0x04; //0000-0100
    private final static char TIMER_ISR = (char) 0x50;

    private final static char SERIAL_MASK = (char) 0x08; //0000-1000
    private final static char SERIAL_ISR = (char) 0x58;

    private final static char JOYPAD_MASK = (char) 0x10; //0001-0000
    private final static char JOYPAD_ISR = (char) 0x60;

    public final static char[] INTERRUPTION_MASKS = new char[]{VBLANK_MASK, LCD_MASK, TIMER_MASK, SERIAL_MASK, JOYPAD_MASK};

    public final static char[] ISR = new char[]{
            0x00,   //0x00 -> Does not exist
            VBLANK_ISR, //0x01 -> VBLANK
            LCD_ISR, 0x00, //0x02 -> LCD
            TIMER_ISR, 0x00, 0x00, 0x00,//0x04 -> TIMER
            SERIAL_ISR, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //0x08 -> SERIAL
            JOYPAD_ISR //0x10 -> JOYPAD
    };

    private List<MemoryInterceptor> interceptors = new ArrayList<>();
    private List<MMIODevice> mmioDevices = new ArrayList<>();

    private boolean bootromEnabled = false;

    //TO-DO: redirect external memory and I/O where it corresponds
    private static final Logger log = LogManager.getLogger(Memory.class);

    /**
     * 16-bit (2 Byte) bus, so a (char) is used, as it is unsigned. Short is NOT unsigned.
     * Allows to read/write 8-bit (1 Byte) at a time (byte)
     */

    private Cartridge cartridge;

    private final static int BOOTROM_SIZE_BYTES = 256;

    private byte[] bootrom;

    //Video/tile RAM: 0x8000 to 0x9FFF (8KB)
    private final static char VIDEO_RAM_START = 0x8000;
    private final static int VIDEO_RAM_SIZE_BYTES = 8192;
    private byte[] videoRAM;

    //External Cartridge RAM: 0xA000 - 0xBFFF (8KB)
    private final static char EXTERNAL_RAM_START = 0xA000;

    //Internal RAM: 0xC000 - 0xDFFF (8KB)
    private final static char INTERNAL_RAM_START = 0xC000;
    private final static int INTERNAL_RAM_SIZE_BYTES = 8192;
    private byte[] internalRAM;

    //Echo internal RAM: 0xE000 - 0xFDFF (7680 Bytes)
    private final static char ECHO_RAM_START = 0xE000;

    //Sprite Attribute Memory (OAM): 0xFE00 - 0xFE9F (160 Bytes)
    private final static char SPRITE_RAM_START = 0xFE00;
    private final static int SPRITE_RAM_SIZE_BYTES = 160;
    private byte[] spriteRAM;

    //Unusable zone: 0xFEA0-FEFF
    private final static char UNUSABLE_RAM_START = 0xFEA0;

    //I/O ports mapped RAM: 0xFF00 - 0xFF7F (128 Bytes)
    private final static char IO_RAM_START = 0xFF00;
    private final static int IO_RAM_SIZE_BYTES = 0xFF00;
    private byte[] ioRAM;

    //High-RAM: 0xFF80 - 0xFFFE (127 Bytes)
    private final static char HIGH_RAM_START = 0xFF80;
    private final static int HIGH_RAM_SIZE_BYTES = 127;
    private byte[] highRAM;

    //Interrupt Enable Register: 0xFFFF (1 Byte)
    private final static char INTERRUPT_ENABLE_ADDRESS = 0xFFFF;
    public byte interruptEnable;


    private final static char DISABLE_BOOTROM_ADDRESS = 0xFF50;

    //Interrupt Flags Register: 0xFF0F (1 Byte)
    private final static char INTERRUPT_FLAGS_ADDRESS = 0xFF0f;
    public byte interruptFlags;


    public Memory() {
        clear();
    }

    public void clear() {
        bootrom = new byte[BOOTROM_SIZE_BYTES];
        videoRAM = new byte[VIDEO_RAM_SIZE_BYTES];
        internalRAM = new byte[INTERNAL_RAM_SIZE_BYTES];
        spriteRAM = new byte[SPRITE_RAM_SIZE_BYTES];
        ioRAM = new byte[IO_RAM_SIZE_BYTES];
        highRAM = new byte[HIGH_RAM_SIZE_BYTES];
        interruptEnable = 0x0;
        interruptFlags = 0x0;
        log.debug("Memory cleared");
    }

    public void addInterceptor(MemoryInterceptor interceptor) {
        this.interceptors.add(interceptor);
        interceptor.addMemory(this);
    }

    public void addMMIODevice(MMIODevice interceptor) {
        this.mmioDevices.add(interceptor);
        interceptor.addMemory(this);
    }


    public byte read(char address) {
        return read(address, false);
    }

    public byte read(char address, boolean privileged) {

        byte result = (byte) 0xFF; //Default bus value
        if (address == INTERRUPT_ENABLE_ADDRESS) {
            result = interruptEnable;
        } else if (address == INTERRUPT_FLAGS_ADDRESS) {
            result = interruptFlags;
        } else if (address >= HIGH_RAM_START) {
            result = highRAM[address - HIGH_RAM_START];
        } else if (address >= IO_RAM_START) {
            //Call IO if any
            boolean mmioFound = false;
            for (MMIODevice device : mmioDevices) {
                if (address >= device.startAddress && address <= device.endAddress) {
                    result = device.onRead(address);
                    mmioFound = true;
                }
            }
            if (!mmioFound) {
                result = ioRAM[address - IO_RAM_START]; //Remove when all mapped
            }
        } else if (address >= UNUSABLE_RAM_START) {
            if (log.isWarnEnabled()) {
                log.warn("Read unusable RAM @" + String.format("%04x", (int) address));
            }
            result = (byte) 0xFF; //reads return $FF (which is the "default value" in the main Game Boy data bus).
        } else if (address >= SPRITE_RAM_START) {
            if (canUseOAM() || privileged) {
                result = spriteRAM[address - SPRITE_RAM_START];
            }
        } else if (address >= ECHO_RAM_START) {
            result = internalRAM[address - ECHO_RAM_START];
        } else if (address >= INTERNAL_RAM_START) {
            result = internalRAM[address - INTERNAL_RAM_START];
        } else if (address >= EXTERNAL_RAM_START) {
            result = cartridge.read(address);
        } else if (address >= VIDEO_RAM_START) {
            if (canUseVRAM() || privileged) {
                result = videoRAM[address - VIDEO_RAM_START];
            }
        } else if (bootromEnabled && address <= BOOTROM_SIZE_BYTES) {   //Read bootrom
            result = bootrom[address];
        } else {  //Cartridge mapped memory
            if (cartridge != null) {
                result = cartridge.read(address);
            } else {
                log.error("Attempted to read from cartridge, but not inserted. Returned 0x00");
                result = 0x00;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Read [@" + String.format("%04x", (int) address) + "]=" + String.format("%02x", result));
        }

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

        //Disable bootrom, and 0x00->0xFF starts mapping to cartridge again
        if (address == DISABLE_BOOTROM_ADDRESS && data == 1 && bootromEnabled) {
            bootromEnabled = false;
        } else if (address == INTERRUPT_ENABLE_ADDRESS) {
            interruptEnable = data;
        } else if (address == INTERRUPT_FLAGS_ADDRESS) {
            interruptFlags = data;
        } else if (address >= HIGH_RAM_START) {
            highRAM[address - HIGH_RAM_START] = data;
        } else if (address >= IO_RAM_START) {
            boolean mmioFound = false;
            for (MMIODevice device : mmioDevices) {
                if (address >= device.startAddress && address <= device.endAddress) {
                    device.onWrite(address, data);
                    mmioFound = true;
                }
            }
            if (!mmioFound) {
                ioRAM[address - IO_RAM_START] = data;
            }
        } else if (address >= UNUSABLE_RAM_START) {
            if (log.isWarnEnabled()) {
                log.warn("Ignored writing into unusable RAM @" + String.format("%02x", (int) address));
            }
            return;
        } else if (address >= SPRITE_RAM_START) {
            if (canUseOAM()) {
                spriteRAM[address - SPRITE_RAM_START] = data;
            }
        } else if (address >= ECHO_RAM_START) {
            internalRAM[address - ECHO_RAM_START] = data;
        } else if (address >= INTERNAL_RAM_START) {
            internalRAM[address - INTERNAL_RAM_START] = data;
        } else if (address >= EXTERNAL_RAM_START) {
            cartridge.write(address,data);
        } else if (address >= VIDEO_RAM_START) {
            if (canUseVRAM()) {
                videoRAM[address - VIDEO_RAM_START] = data;
            }
        } else { //Cartridge mapped memory
            if (cartridge != null) {
                cartridge.write(address, data);
            } else {
                log.error("Attempted to write to cartridge, but it is not inserted");
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Write [@" + String.format("%04x", (int) address) + "]=" + String.format("%02x", data));
        }
    }

    public void fireTimerInterruption() {
        this.interruptFlags |= TIMER_MASK;
    }

    public void fireVBlankInterruption() {
        this.interruptFlags |= VBLANK_MASK;
    }

    public void fireLcdInterruption() {
        this.interruptFlags |= LCD_MASK;
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

    private boolean canUseOAM() {
        return !isLcdEnabled() || (!isGPUOamMode() && !isGPUVramMode());
    }

    private boolean canUseVRAM() {
        return !isLcdEnabled() || !isGPUVramMode();
    }

    private boolean isGPUOamMode() {
        int mode = read((char) (0xFF41)) & 0x03;
        return mode == 2;
    }

    private boolean isGPUVramMode() {
        int mode = read((char) (0xFF41)) & 0x03;
        return mode == 3;
    }

    private boolean isLcdEnabled() {
        int mode = read((char) (0xFF40)) & 0x80;
        return mode != 0;
    }

    public void setBootrom(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.isFile() || !file.canRead()) {
            throw new Exception("Cannot read file " + filePath);
        }
        byte[] content = Files.readAllBytes(file.toPath());
        if (content.length > 256) {
            throw new Exception("Bootrom file cannot fit! Max is 256B, file is " + content.length + "B");
        }
        for (int i = 0; i < content.length; i++) {
            bootrom[i] = content[i];
        }
        bootromEnabled = true;
    }
}
