package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.Registers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {

    //This should be used instead of assertEquals, as that casts the byte to an int, and causes problems!!
    public static void assertEquals8(int a, byte b) {
        assertEquals((byte) a, b);
    }

    public static void assertEquals16(int a, char b) {
        assertEquals((char) a, b);
    }

    public static byte getOpcode(int opcode, byte first, byte second) {
        byte res = 0x0; //00 000 000
        res = (byte) (res | (opcode << 6)); //OP 000 000
        res = (byte) (res | ((first & 0x7) << 3));//OP FST 000
        res = (byte) (res | (second & 0x7)); //OP FST SND
        return res;
    }

    public static void assertFlags(Registers registers, boolean z, boolean n, boolean h, boolean c) {
        assertTrue(registers.checkFlagZ() == z && registers.checkFlagN() == n && registers.checkFlagH() == h && registers.checkFlagC() == c);
    }
}
