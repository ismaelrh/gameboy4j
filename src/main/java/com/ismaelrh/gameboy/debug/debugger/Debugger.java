package com.ismaelrh.gameboy.debug.debugger;

import com.ismaelrh.gameboy.cpu.ControlUnit;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class Debugger {

    private static final Logger log = LogManager.getLogger(ControlUnit.class);

    private final Memory memory;
    private final Registers registers;
    private DebuggerController controller;

    //Debugger status
    private final Set<Character> breakpoints = new HashSet<>();
    private boolean breakpointsEnabled = true;
    private Boolean isPaused = false;
    private boolean stepMode = false; //Whether step by step mode is activated

    public Debugger(Memory memory, Registers registers) {
        this.memory = memory;
        this.registers = registers;
    }

    public void setController(DebuggerController controller) {
        this.controller = controller;
        controller.init(memory, registers, this);
    }

    public void debug() throws InterruptedException {

        if (controller != null) {
            controller.onChange();
        }

        if (stepMode) {
            log.info("CPU paused at " + String.format("%04X", (int) registers.getPC()));
            pauseSystem();
        }

        if (breakpointsEnabled && breakpoints.contains(registers.getPC())) {
            log.info("Stop at breakpoint " + String.format("%04X", (int) registers.getPC()));
            pauseSystem();
        }

    }

    public void addBreakpoint(char address) {
        this.breakpoints.add(address);
        log.info("Breakpoint added at " + String.format("%04X", (int) address));
    }

    public void removeBreakpoint(char address) {
        this.breakpoints.remove(address);
        log.info("Breakpoint removed at " + String.format("%04X", (int) address));
    }

    public void removeAllBreakpoints() {
        this.breakpoints.clear();
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
