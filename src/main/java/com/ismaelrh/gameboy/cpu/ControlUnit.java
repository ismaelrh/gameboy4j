package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.instructions.InstDecoder;
import com.ismaelrh.gameboy.cpu.instructions.InstDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is responsible for executing a single instruction.
 */
public class ControlUnit {

    private static final Logger log = LogManager.getLogger(ControlUnit.class);


    private Registers registers;
    private Memory memory;
    private InstDecoder decoder;

    public ControlUnit(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
        this.decoder = new InstDecoder();
    }

    public int runInstruction() throws Exception {

        boolean isCB = false;

        //Fetch 1st op byte
        byte opcode = readAndIncPC();
        if (opcode == (byte) 0xCB) { //Prefix operation
            opcode = readAndIncPC();
            isCB = true;
        }

        //Decode
        InstDescription instDescription = decoder.getInst(isCB, opcode);

        Instruction inst = new Instruction(opcode);

        if (isCB) {
            inst.setPrefix((byte) 0xCB);
        }

        //Fetch (2nd step, as it is already decoded)
        if (instDescription.getExtraBytes() == 1 || instDescription.getExtraBytes() == 2) {
            inst.setNn1(readAndIncPC());
        }
        if (instDescription.getExtraBytes() == 2) {
            inst.setNn2(readAndIncPC());
        }

        String prefix = isCB ? "CB "  : "";
        System.out.println(String.format("Instruction %s0x%02X - %s - extra=[0x%02X,0x%02X]", prefix,opcode, instDescription.getMnemonic(), inst.getNn1(), inst.getNn2()));


        //Execute and return the number of cycles that it took
        return instDescription.getInst().apply(inst, memory, registers);
    }

    private byte readAndIncPC() {
        byte res = this.memory.read(this.registers.getPC());
        this.registers.setPC((char) (this.registers.getPC() + 1));
        return res;
    }


}
