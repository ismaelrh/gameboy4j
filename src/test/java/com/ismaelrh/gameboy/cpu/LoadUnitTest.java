package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Memory;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.assertEquals8;
import static com.ismaelrh.gameboy.TestUtils.makeInst;
import static com.ismaelrh.gameboy.cpu.Registers.A;
import static com.ismaelrh.gameboy.cpu.Registers.B;
import static com.ismaelrh.gameboy.cpu.Registers.H;
import static com.ismaelrh.gameboy.cpu.Registers.NONE;
import static org.junit.Assert.assertEquals;

public class LoadUnitTest {

	private LoadUnit loadUnit;
	private Registers registers;
	private Memory memory;

	@Before
	public void setUp() {
		registers = new Registers();
		memory = new Memory();
		loadUnit = new LoadUnit(registers, memory);
		registers.setA((byte) 0xFF);
		registers.setB((byte) 0xFF);
		registers.setC((byte) 0xFF);
		registers.setD((byte) 0xFF);
		registers.setE((byte) 0xFF);
		registers.setH((byte) 0xFF);
		registers.setL((byte) 0xFF);
	}

	@Test
	public void loadRR() {
		//b <- a, h <- b
		registers.setA((byte) 0x12);

		short cycles = loadUnit.loadRR(makeInst(0x3, B, A));
		loadUnit.loadRR(makeInst(0x3, H, B));
		assertEquals8(0x12, registers.getB());
		assertEquals8(0x12, registers.getH());
		assertEquals(4, cycles);

	}

	@Test
	public void loadRImmediate() {
		//b <- 0xBE
		//h <- 0xEF
		short cycles = loadUnit.loadRImmediate(makeInst(0x3, B, NONE), (byte) 0xBE);
		loadUnit.loadRImmediate(makeInst(0x3, H, NONE), (byte) 0xEF);
		assertEquals8(0xBE, registers.getB());
		assertEquals8(0xEF, registers.getH());
		assertEquals(8, cycles);

	}

	@Test
	public void loadRHL() {
		//HL = 0xC003, (HL) = 0x12, b <- 0x12
		memory.write((char) 0xC003, (byte) 0x12);
		registers.setHL((char) 0xC003);

		short cycles = loadUnit.loadRHL(makeInst(0x3, B, NONE));
		assertEquals8(0x12, registers.getB());
		assertEquals(8, cycles);
	}

	@Test
	public void loadHLR() {
		//HL = 0xC003, b = 0x12, (HL) <- 0x12
		registers.setHL((char) 0xC003);
		registers.setB((byte) 0x12);
		short cycles = loadUnit.loadHLR(makeInst(0x3, NONE, B));
		assertEquals8(0x12, memory.read((char) 0xC003));
		assertEquals(8, cycles);
	}

	@Test
	public void loadHLN() {
		//HL = 0xC003, (HL) <- 0x12
		registers.setHL((char) 0xC003);
		short cycles = loadUnit.loadHLN((byte) 0x12);
		assertEquals8(0x12, memory.read((char) 0xC003));
		assertEquals(12, cycles);
	}

	@Test
	public void loadA_BC() {
		//BC = 0xC003, (BC) = 0x2F, A <- 0x2F
		registers.setBC((char) 0xC003);
		memory.write((char) 0xC003, (byte) 0x2F);
		short cycles = loadUnit.loadA_BC();

		assertEquals8(0x2F, registers.getA());
		assertEquals(8, cycles);
	}

	@Test
	public void loadA_DE() {
		//BC = 0xC003, (BC) = 0x5F, A <- 0x5F
		registers.setDE((char) 0xC003);
		memory.write((char) 0xC003, (byte) 0x5F);
		short cycles = loadUnit.loadA_DE();

		assertEquals8(0x5F, registers.getA());
		assertEquals(8, cycles);
	}

	@Test
	public void loadA_nn() {
		//(0xC003) <- 0x12, A <- 0x12
		memory.write((char) 0xC003, (byte) 0x12);
		short cycles = loadUnit.loadA_nn((char) 0xC003);

		assertEquals8(0x12, registers.getA());
		assertEquals(16, cycles);
	}

	@Test
	public void loadBC_A() {
		//BC = 0xC003, A = 0x3F, (BC) <- 0x3F
		registers.setBC((char) 0xC003);
		registers.setA((byte) 0x3F);
		short cycles = loadUnit.loadBC_A();
		assertEquals8(0x3F, memory.read((char) 0xc003));
		assertEquals(8, cycles);
	}


	@Test
	public void loadDE_A() {
		//de = 0xC003, A = 0x3F, (de) <- 0x3F
		registers.setDE((char) 0xC003);
		registers.setA((byte) 0x3F);
		short cycles = loadUnit.loadDE_A();
		assertEquals8(0x3F, memory.read((char) 0xc003));
		assertEquals(8, cycles);
	}

	@Test
	public void loadNN_A() {
		//NN= 0xC003, A = 0x3F, (BC) <- 0x3F
		registers.setA((byte) 0x3F);
		short cycles = loadUnit.loadNN_A((char) 0xC003);
		assertEquals8(0x3F, memory.read((char) 0xc003));
		assertEquals(16, cycles);
	}

	@Test
	public void loadA_C() {

	}

}
