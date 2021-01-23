package com.ismaelrh.gameboy.debug.debugger.console;

import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.debug.debugger.Debugger;
import com.ismaelrh.gameboy.debug.debugger.DebuggerController;

/**
 * Debugger interface in console.
 */
public class ConsoleController implements DebuggerController {

    private Debugger debugger;
    private Memory memory;
    private Registers registers;
    private RegisterStatus registerStatus;

    @Override
    public void init(Memory memory, Registers registers, Debugger debugger) {
        this.debugger = debugger;
        this.memory = memory;
        this.registers = registers;
        this.registerStatus = new RegisterStatus(registers);
        //Launch thread
        launchListenerThread();
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onStop() {

    }

    private void launchListenerThread() {
        new Thread(new ConsoleListenerThread(this)).start();
    }

    protected void parseCommand(String command) throws InterruptedException {
        String[] parts = command.toLowerCase().trim().split(" ");
        switch (parts[0]) {
            case "bp":
                parseBreakCommand(parts);
                break;
            case "step":
                debugger.stepExecution();
                break;
            case "continue":
                debugger.continueExecution();
                break;
            case "pause":
                debugger.pause();
                break;
            case "status":
                printRegisterStatus();
                break;
            default:
                System.err.println("Command not recognized: " + parts[0]);
        }
    }

    private void parseBreakCommand(String[] parts) {
        if (parts[1].equals("add")) {
            char address = (char) (Integer.parseInt(parts[2], 16) & 0xFFFF);
            debugger.addBreakpoint(address);
        } else if (parts[1].equals("on")) {
            debugger.enableBreakpoints();
        } else if (parts[1].equals("off")) {
            debugger.disableBreakpoints();
        } else if (parts[1].equals("rm")) {
            if (parts[2].equals("all")) {
                debugger.removeAllBreakpoints();
            } else {
                char address = (char) (Integer.parseInt(parts[2], 16) & 0xFFFF);
                debugger.removeBreakpoint(address);
            }
        }
    }

    private void printRegisterStatus() {
        registerStatus.print();
    }

}
