package com.ismaelrh.gameboy.debug.debugger;

import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.memory.Memory;

//This implements how to interact with debugger, in a different thread.
public interface DebuggerController {

    void init(Memory memory, Registers registers, Debugger debugger);

    void onChange();

    void onStop();
}
