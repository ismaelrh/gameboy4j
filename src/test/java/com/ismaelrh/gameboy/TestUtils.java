package com.ismaelrh.gameboy;

import static org.junit.Assert.assertEquals;

public class TestUtils {

	public static void assertEquals8(int a, byte b) {
		assertEquals((byte) a, b);
	}

	public static void assertEquals16(int a, char b) {
		assertEquals((char) a, b);
	}

	public static byte makeInst(int opcode, byte first, byte second) {
		byte res = 0x0; //00 000 000
		res = (byte) (res | (opcode << 6)); //OP 000 000
		res = (byte) (res | ((first & 0x7) << 3));//OP FST 000
		res = (byte) (res | (second & 0x7)); //OP FST SND
		return res;
	}
}
