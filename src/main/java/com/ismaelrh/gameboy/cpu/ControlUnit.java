package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import com.ismaelrh.gameboy.cpu.instructions.InstDecoder;
import com.ismaelrh.gameboy.cpu.instructions.InstDescription;

public class ControlUnit {

    private Registers registers;
    private Memory memory;
    private InstDecoder decoder;

    public ControlUnit(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
        this.decoder = new InstDecoder();
    }

    private void initialize() {

    }

    private void loadCartridge() {

    }

    private void loop() throws Exception {

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

        //Execute
        int cycles = instDescription.getInst().apply(inst, memory, registers);

        //Wait appropriate time
    }

    private byte readAndIncPC() {
        byte res = this.memory.read(this.registers.getPC());
        this.registers.setPC((char) (this.registers.getPC() + 1));
        return res;
    }


}
