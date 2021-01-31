package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.instructions.InstDecoder;
import com.ismaelrh.gameboy.cpu.instructions.InstDescription;
import com.ismaelrh.gameboy.debug.debugger.Debugger;
import com.ismaelrh.gameboy.debug.debugger.DebuggerController;
import com.ismaelrh.gameboy.debug.logCheck.LogStatusProvider;
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
    private Debugger debugger;
    private ExecutionInfo executionInfo;

    public ControlUnit(Registers registers, Memory memory) {
        this.registers = registers;
        this.memory = memory;
        this.decoder = new InstDecoder();
        this.executionInfo = new ExecutionInfo();
        this.debugger = new Debugger(memory, registers, executionInfo);
    }

    public void setDebuggerController(DebuggerController controller) {
        this.debugger.setController(controller);
    }

    public void setLogStatusProvider(LogStatusProvider logStatusProvider) {
        this.debugger.setLogStatusProvider(logStatusProvider);
    }

    public int runInstruction() throws Exception {

        Instruction instruction = readInstruction();
        InstDescription description = instruction.getDescription();

        executionInfo.setCurrentInstruction(instruction);
        debugger.debug();   //This can block the execution

        //Then, increment PC as needed
        registers.setPC((char) (registers.getPC() + instruction.getInstBytes()));

        //Execute and return the number of cycles that it took
        int instCycles = description.getInst().apply(instruction, memory, registers);
        executionInfo.addCycles(instCycles);
        return instCycles;
    }

    //Does NOT increment PC
    private Instruction readInstruction() {

        char initialPC = registers.getPC();
        char auxPC = initialPC;

        boolean isCB = false;

        //Fetch 1st op byte
        byte opcode = this.memory.read(auxPC);
        auxPC += 1;

        if (opcode == (byte) 0xCB) { //Prefix operation
            opcode = this.memory.read(auxPC);
            auxPC += 1;
            isCB = true;
        }

        //Decode
        InstDescription instDescription = decoder.getInst(isCB, opcode);
        Instruction inst = new Instruction(opcode);
        inst.setDescription(instDescription);

        if (isCB) {
            inst.setPrefix((byte) 0xCB);
        }

        //Fetch (2nd step, as it is already decoded)
        if (instDescription.getExtraBytes() == 1 || instDescription.getExtraBytes() == 2) {
            inst.setNn1(this.memory.read(auxPC));
            auxPC += 1;
        }
        if (instDescription.getExtraBytes() == 2) {
            inst.setNn2(this.memory.read(auxPC));
            auxPC += 1;
        }
        inst.setInstBytes(auxPC - initialPC);
        return inst;
    }

    private byte readAndIncPC() {
        byte res = this.memory.read(this.registers.getPC());
        this.registers.setPC((char) (this.registers.getPC() + 1));
        return res;
    }


}
