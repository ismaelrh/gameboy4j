package com.ismaelrh.gameboy.cpu;

import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertEquals16;
import static com.ismaelrh.gameboy.TestUtils.assertEquals8;
import static org.junit.Assert.assertEquals;

public class RegistersTest {

	private Registers registers = new Registers();

	@Test
	public void readsAndWritesSP() {
		registers.setSP((char) 0xBEEF);
		assertEquals(0xBEEF, registers.getSP());
	}

	@Test
	public void readsAndWritesPC() {
		registers.setPC((char) 0xBEEF);
		assertEquals(0xBEEF, registers.getPC());
	}

	@Test
	public void readsAndWritesAF() {
		registers.setAF((char) 0xBEEF);
		assertEquals16(0xBEE0, registers.getAF());
		assertEquals8(0xBE, registers.getA());
		assertEquals8(0xE0, registers.getF());
	}

	@Test
	public void readsAndWritesAFseparately() {
		registers.setAF((char) 0xFFFF);

		registers.setA((byte) 0x12);
		assertEquals8(0x12, registers.getA());
		assertEquals16(0x12F0, registers.getAF());

		registers.setF((byte) 0x34);
		assertEquals8(0x30, registers.getF());
		assertEquals16(0x1230, registers.getAF());
	}

	@Test
	public void readsAndWritesF() {
		registers.setF((byte) 0x12);
		assertEquals8(0x10, registers.getF());
	}

	@Test
	public void readsAndWritesBC() {
		registers.setBC((char) 0xBEEF);
		assertEquals16(0xBEEF, registers.getBC());
		assertEquals8(0xBE, registers.getB());
		assertEquals8(0xEF, registers.getC());
	}

	@Test
	public void readsAndWritesBCseparately() {
		registers.setBC((char) 0xFFFF);

		registers.setB((byte) 0x12);
		assertEquals8(0x12, registers.getB());
		assertEquals16(0x12FF, registers.getBC());

		registers.setC((byte) 0x34);
		assertEquals8(0x34, registers.getC());
		assertEquals16(0x1234, registers.getBC());
	}

	@Test
	public void readsAndWritesDE() {
		registers.setDE((char) 0xBEEF);
		assertEquals16(0xBEEF, registers.getDE());
		assertEquals8(0xBE, registers.getD());
		assertEquals8(0xEF, registers.getE());
	}

	@Test
	public void readsAndWritesDEseparately() {
		registers.setDE((char) 0xFFFF);

		registers.setD((byte) 0x12);
		assertEquals8(0x12, registers.getD());
		assertEquals16(0x12FF, registers.getDE());

		registers.setE((byte) 0x34);
		assertEquals8(0x34, registers.getE());
		assertEquals16(0x1234, registers.getDE());
	}

	@Test
	public void readsAndWritesHL() {
		registers.setHL((char) 0xBEEF);
		assertEquals16(0xBEEF, registers.getHL());
		assertEquals8(0xBE, registers.getH());
		assertEquals8(0xEF, registers.getL());
	}

	@Test
	public void readsAndWritesHLseparately() {
		registers.setHL((char) 0xFFFF);

		registers.setH((byte) 0x12);
		assertEquals8(0x12, registers.getH());
		assertEquals16(0x12FF, registers.getHL());

		registers.setL((byte) 0x34);
		assertEquals8(0x34, registers.getL());
		assertEquals16(0x1234, registers.getHL());
	}


}
