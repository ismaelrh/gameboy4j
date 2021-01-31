package com.ismaelrh.gameboy.debug.debugger;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.cpu.ControlUnit;
import com.ismaelrh.gameboy.cpu.ExecutionInfo;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.instructions.InstDescription;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.debug.logCheck.LogStatus;
import com.ismaelrh.gameboy.debug.logCheck.LogStatusProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class Debugger {

    private static final Logger log = LogManager.getLogger(ControlUnit.class);

    private final Memory memory;
    private final Registers registers;
    private DebuggerController controller;
    private ExecutionInfo executionInfo;
    private LogStatusProvider logStatusProvider;

    //Debugger status
    private final Set<Character> breakpoints = new HashSet<>();
    private final Set<Integer> cycleBreakpoints = new HashSet<>();
    private final Set<String> instructionBreakpoints = new HashSet<>();


    private boolean breakpointsEnabled = true;
    private Boolean isPaused = false;
    private boolean stepMode = false; //Whether step by step mode is activated

    public Debugger(Memory memory, Registers registers, ExecutionInfo info) {
        this.memory = memory;
        this.registers = registers;
        this.executionInfo = info;
    }

    public void setController(DebuggerController controller) {
        this.controller = controller;
        controller.init(memory, registers, this, executionInfo);
        //addBreakpoint((char) (0x200));
    }

    public void setLogStatusProvider(LogStatusProvider logStatusProvider) {
        this.logStatusProvider = logStatusProvider;
    }

    public void debug() throws InterruptedException {

        if (controller != null) {
            controller.onChange();
        }

        //Check with logs
        if (this.logStatusProvider != null) {
            LogStatus status = logStatusProvider.next();
            if (!status.isOk(executionInfo.getCycles(), registers)) {
                log.error("Logs are not the same as current execution status!");
                status.printDiff(executionInfo.getCycles(), registers);
                pauseSystem();
            }
        }

        if (stepMode) {
            log.info("CPU paused at " + String.format("%04X", (int) registers.getPC()));
            pauseSystem();
        }

        if (breakpointsEnabled && breakpoints.contains(registers.getPC())) {
            log.info("Stop at breakpoint " + String.format("%04X", (int) registers.getPC()));
            pauseSystem();
        }

        if (breakpointsEnabled && cycleBreakpoints.contains(executionInfo.getCycles())) {
            log.info("Stop at breakpoint " + executionInfo.getCycles() + " cycles");
            pauseSystem();
        }

        if (breakpointsEnabled && meetsInstructionCheckpoint(executionInfo.getCurrentInstruction())) {
            log.info("Stop at instruction " + executionInfo.getCurrentInstruction().getInstrBytes());
            pauseSystem();
        }

    }

    private boolean meetsInstructionCheckpoint(Instruction inst) {

        String pattern = "";
        String[] pairs = inst.getInstrBytes().split(" ");

        for (String pair : pairs) {
            pattern = (pattern + " " + pair).trim();
            if (instructionBreakpoints.contains(pattern)) {
                return true;
            }
        }
        return false;

    }

    public void addBreakpoint(char address) {
        this.breakpoints.add(address);
        log.info("Breakpoint added at " + String.format("%04X", (int) address));
    }

    public void addCyclesBreakpoint(int cycles) {
        this.cycleBreakpoints.add(cycles);
        log.info("Breakpoint added at " + cycles + " cycles");
    }

    public void addInstructionBreakpoint(String instruction) {
        this.instructionBreakpoints.add(instruction);
        log.info("Breakpoint added for instruction " + instruction);
    }

    public void removeInstructionBreakpoint(String instruction) {
        this.instructionBreakpoints.remove(instruction);
        log.info("Breakpoint removed for instruction " + instruction);
    }

    public void removeBreakpoint(char address) {
        this.breakpoints.remove(address);
        log.info("Breakpoint removed at " + String.format("%04X", (int) address));
    }

    public void removeCycleBreakpoint(int cycles) {
        this.cycleBreakpoints.remove(cycles);
        log.info("Breakpoint removed at " + cycles + " cycles");
    }

    public void removeAllBreakpoints() {
        this.breakpoints.clear();
        this.cycleBreakpoints.clear();
        log.info("All breakpoints removed");
    }

    public void enableBreakpoints() {
        this.breakpointsEnabled = true;
        log.info("Breakpoints enabled");
    }

    public void disableBreakpoints() {
        this.breakpointsEnabled = false;
        log.info("Breakpoints disabled");
    }

    public void stepExecution() {
        this.stepMode = true;
        unpauseSystem();
    }

    public void continueExecution() {
        this.stepMode = false;
        unpauseSystem();
    }

    public void pause() throws InterruptedException {
        stepMode = true;
    }

    private synchronized void pauseSystem() throws InterruptedException {
        controller.onStop();
        this.isPaused = true;
        while (isPaused) {
            wait();
        }
    }

    private synchronized void unpauseSystem() {
        isPaused = false;
        notify();
    }


}
