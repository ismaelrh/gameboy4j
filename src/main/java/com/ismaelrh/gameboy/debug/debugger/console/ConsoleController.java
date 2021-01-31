package com.ismaelrh.gameboy.debug.debugger.console;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.cpu.ExecutionInfo;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.debug.debugger.Debugger;
import com.ismaelrh.gameboy.debug.debugger.DebuggerController;

import java.util.Arrays;

/**
 * Debugger interface in console.
 */
public class ConsoleController implements DebuggerController {

    private Debugger debugger;
    private Memory memory;
    private Registers registers;
    private RegisterStatus registerStatus;
    private ExecutionInfo executionInfo;
    private Instruction currentInstruction;

    @Override
    public void init(Memory memory, Registers registers, Debugger debugger, ExecutionInfo info) {
        this.debugger = debugger;
        this.memory = memory;
        this.registers = registers;
        this.executionInfo = info;
        this.registerStatus = new RegisterStatus(registers, executionInfo);
        //Launch thread
        launchListenerThread();
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onStop() {
        printRegisterStatus();
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
            case "bpc":
                parseBreakCycleCommand(parts);
                break;
            case "bpi":
                parseInstCycleCommand(parts);
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
            case "mem":
                parseMemCommand(parts[1]);
                break;
            default:
                System.err.println("Command not recognized: " + parts[0]);
        }
    }

    private void parseMemCommand(String add) {
        char address = (char) (Integer.parseInt(add, 16) & 0xFFFF);
        byte value = memory.read(address);
        System.out.println("Read " + f(value) + " at @" + f(address));
    }

    private void parseBreakCycleCommand(String[] parts) {
        if (parts[1].equals("add")) {
            debugger.addCyclesBreakpoint(Integer.parseInt(parts[2]));
        } else if (parts[1].equals("rm")) {
            debugger.removeCycleBreakpoint(Integer.parseInt(parts[2]));
        }
    }

    private void parseInstCycleCommand(String[] parts) {
        String instruction = "";
        for (int i = 2; i < parts.length; i++) {
            instruction += parts[i] + " ";
        }
        instruction = instruction.trim();
        if (parts[1].equals("add")) {
            debugger.addInstructionBreakpoint(instruction);
        } else if (parts[1].equals("rm")) {
            debugger.removeInstructionBreakpoint(instruction);
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

    private String f(char data) {
        return String.format("%04X", (int) data);
    }

    private String f(byte data) {
        return String.format("%02X", data);
    }
}
