package com.ismaelrh.gameboy.cpu;

import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertEquals8;
import static com.ismaelrh.gameboy.TestUtils.assertEquals16;
import static org.junit.Assert.assertEquals;

public class ProcessUnitTest {

	private ProcessUnit processUnit = new ProcessUnit();

	@Test
	public void readsAndWritesSP() {
		processUnit.setSP((char) 0xBEEF);
		assertEquals(0xBEEF, processUnit.getSP());
	}

	@Test
	public void readsAndWritesPC() {
		processUnit.setPC((char) 0xBEEF);
		assertEquals(0xBEEF, processUnit.getPC());
	}

	@Test
	public void readsAndWritesA() {
		processUnit.setA((byte) 0x12);
		assertEquals8(0x12, processUnit.getA());
	}

	@Test
	public void readsAndWritesF() {
		processUnit.setF((byte) 0x12);
		assertEquals8(0x12, processUnit.getF());
	}

	@Test
	public void readsAndWritesBC() {
		processUnit.setBC((char) 0xBEEF);
		assertEquals16(0xBEEF, processUnit.getBC());
		assertEquals8(0xBE, processUnit.getB());
		assertEquals8(0xEF, processUnit.getC());
	}

	@Test
	public void readsAndWritesBCseparately() {
		processUnit.setBC((char) 0xFFFF);

		processUnit.setB((byte) 0x12);
		assertEquals8(0x12, processUnit.getB());
		assertEquals16(0x12FF, processUnit.getBC());

		processUnit.setC((byte) 0x34);
		assertEquals8(0x34, processUnit.getC());
		assertEquals16(0x1234, processUnit.getBC());
	}

	@Test
	public void readsAndWritesDE() {
		processUnit.setDE((char) 0xBEEF);
		assertEquals16(0xBEEF, processUnit.getDE());
		assertEquals8(0xBE, processUnit.getD());
		assertEquals8(0xEF, processUnit.getE());
	}

	@Test
	public void readsAndWritesDEseparately() {
		processUnit.setDE((char) 0xFFFF);

		processUnit.setD((byte) 0x12);
		assertEquals8(0x12, processUnit.getD());
		assertEquals16(0x12FF, processUnit.getDE());

		processUnit.setE((byte) 0x34);
		assertEquals8(0x34, processUnit.getE());
		assertEquals16(0x1234, processUnit.getDE());
	}

	@Test
	public void readsAndWritesHL() {
		processUnit.setHL((char) 0xBEEF);
		assertEquals16(0xBEEF, processUnit.getHL());
		assertEquals8(0xBE, processUnit.getH());
		assertEquals8(0xEF, processUnit.getL());
	}

	@Test
	public void readsAndWritesHLseparately() {
		processUnit.setHL((char) 0xFFFF);

		processUnit.setH((byte) 0x12);
		assertEquals8(0x12, processUnit.getH());
		assertEquals16(0x12FF, processUnit.getHL());

		processUnit.setL((byte) 0x34);
		assertEquals8(0x34, processUnit.getL());
		assertEquals16(0x1234, processUnit.getHL());
	}



}
