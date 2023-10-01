package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.gpu.lcd.Lcd;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class TestLcd extends Lcd {

    private final int[] pixels = new int[160 * 144];
    private int[] frozenPixels = new int[160 * 144];

    private int nextPixel = 0;

    private boolean enabled = true;

    public TestLcd() {
        flush();
    }

    @Override
    public void pushPixel(int color) {
        if (nextPixel < 160 * 144) {
            pixels[nextPixel] = color;
            nextPixel++;
        }
    }

    @Override
    public void flush() {
        nextPixel = 0;
        frozenPixels = Arrays.copyOf(pixels, pixels.length);
    }

    @Override
    public void disableLcd() {
        enabled = false;
    }

    @Override
    public void enableLcd() {
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns a MD5 hash of the screen at this moment,
     * using the colors code, instead of the actual colors.
     */
    @Override
    public String getHash() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4 * pixels.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        for (int pixel : pixels) {
            buffer.putInt(getColorCode(pixel));
        }
        byte[] hash = MessageDigest.getInstance("MD5").digest(buffer.array());
        return Base64.getEncoder().encodeToString(hash);
    }


}
