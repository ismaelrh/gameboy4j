package com.ismaelrh.gameboy.cpu;

import com.ismaelrh.gameboy.cpu.instruction.Instruction;
import com.ismaelrh.gameboy.cpu.instruction.InstructionFactory;
import com.ismaelrh.gameboy.cpu.instructions.implementation.JumpCommands;
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

        if (registers.isHalt()) {
            return 4;
        }

        Instruction instruction = readInstruction();
        InstDescription description = instruction.getDescription();

        executionInfo.setCurrentInstruction(instruction);
        debugger.debug();   //This can block the execution

        //Then, increment PC as needed
        registers.setPC((char) (registers.getPC() + instruction.getInstBytes()));

        //Execute and return the number of cycles that it took
        int instCycles = description.getInst().apply(instruction, memory, registers);
        executionInfo.addCycles(instCycles);

        //Release instruction object
        InstructionFactory.releaseInstruction(instruction);
        return instCycles;
    }

    public void checkInterruptions() {

        //You can exit halt mode even if IME is disabled, just if there are interruptions that could be serviced
        if (memory.interruptEnable != 0 && memory.interruptFlags != 0 && registers.isHalt()) {
            registers.setHalt(false);
        }

        //Global IME activated, some interrupt activated, and some interrupt fired
        if (registers.isIme() && memory.interruptEnable != 0 && memory.interruptFlags != 0) {

            //Filter the ones that have to be attended
            byte interruptionsToFire = (byte) ((memory.interruptEnable & memory.interruptFlags) & 0xFF);

            //Ordered by priority,from lowest bit (vblank) to highest bit (joypad)
            for (char mask : Memory.INTERRUPTION_MASKS) {
                if ((interruptionsToFire & mask) != 0) {  //This should be attended
                    registers.setIme(false);
                    JumpCommands.doCall(Memory.ISR[mask], memory, registers);
                    memory.interruptFlags &= ~mask;
                    return;
                }
            }

        }
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
        Instruction inst = InstructionFactory.getInstruction(opcode);
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
