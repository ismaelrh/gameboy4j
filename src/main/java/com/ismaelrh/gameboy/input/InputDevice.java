package com.ismaelrh.gameboy.input;

import com.ismaelrh.gameboy.cpu.memory.MMIODevice;
import com.ismaelrh.gameboy.cpu.memory.Memory;

public class InputDevice extends MMIODevice {

    //State. Start is 0xCF, means high half is 1100, both modes are selected
    private boolean actionButtonsSelected = true;
    private boolean directionButtonsSelected = true;

    private final InputState inputState;
    private final Memory memory;

    public InputDevice(InputState inputState, Memory memory) {
        super((char) 0xFF00, (char) 0xFF00);
        this.inputState = inputState;
        this.memory = memory;
        this.inputState.registerCallback(memory::fireJoypadInterruption);
    }

    @Override
    public void onWrite(char address, byte data) {
        byte bit5 = (byte) ((data >> 5) & 0x01);
        byte bit4 = (byte) ((data >> 4) & 0x01);
        actionButtonsSelected = bit5 == 0x00;
        directionButtonsSelected = bit4 == 0x00;
    }

    @Override
    public byte onRead(char address) {
        boolean input3pressed = !((actionButtonsSelected && inputState.startPressed()) || (directionButtonsSelected && inputState.downPressed()));
        boolean input2pressed = !((actionButtonsSelected && inputState.isSelect()) || (directionButtonsSelected && inputState.upPressed()));
        boolean input1pressed = !((actionButtonsSelected && inputState.bPressed()) || (directionButtonsSelected && inputState.leftPressed()));
        boolean input0pressed = !((actionButtonsSelected && inputState.aPressed()) || (directionButtonsSelected && inputState.rightPressed()));
        return generateByte(input3pressed, input2pressed, input1pressed, input0pressed);
    }

    private byte generateByte(boolean bit3, boolean bit2, boolean bit1, boolean bit0) {
        byte result = (byte) 0x00;   //Starts is 0xCF
        if (bit3) result = (byte) (result | 0x08);
        if (bit2) result = (byte) (result | 0x04);
        if (bit1) result = (byte) (result | 0x02);
        if (bit0) result = (byte) (result | 0x01);
        return (byte) (result & 0xFF);
    }
}
