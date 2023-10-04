package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.Const;
import com.ismaelrh.gameboy.cpu.ControlUnit;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import com.ismaelrh.gameboy.cpu.cartridge.CartridgeFactory;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.periphericals.timer.Timer;
import com.ismaelrh.gameboy.debug.debugger.DebuggerController;
import com.ismaelrh.gameboy.gpu.Gpu;
import com.ismaelrh.gameboy.gpu.lcd.Lcd;
import com.ismaelrh.gameboy.input.InputDevice;
import com.ismaelrh.gameboy.input.InputState;

import java.util.ArrayList;
import java.util.List;

public class GameBoy {

    private final Memory memory;
    private final Registers registers;
    private final Timer timer;
    private final Gpu gpu;
    private final ControlUnit controlUnit;
    private final InputDevice inputDevice;

    private final Lcd lcd;
    private Cartridge cartridge;
    private List<FrameFinishedListener> frameFinishedListeners = new ArrayList<>();
    private long totalCycles;

    private volatile boolean paused = false;


    public GameBoy(Lcd lcd) {
        this.memory = new Memory();
        this.registers = new Registers();
        registers.initForRealGB();

        this.timer = new Timer(memory);
        this.lcd = lcd;
        this.gpu = new Gpu(memory, lcd);
        this.controlUnit = new ControlUnit(registers, memory);

        this.inputDevice = new InputDevice(memory);

        memory.addMMIODevice(timer);
        memory.addMMIODevice(gpu);
        memory.addMMIODevice(inputDevice);
    }

    public void setBootrom(String path) throws Exception {
        memory.setBootrom(path);
        registers.setPC((char) 0x0000);
    }

    public void loadCartridge(String path) throws Exception {
        //TODO: derive path of save
        this.cartridge = CartridgeFactory.create(path,getSaveName(path));
        memory.insertCartridge(cartridge);
    }

    private String getSaveName(String fullPath){
        return getPath(  fullPath) + "/" + getFileName(fullPath) + ".sav";
    }
    private String getPath(String fullPath){
        int lastSlash = fullPath.lastIndexOf("/");
        return fullPath.substring(0, lastSlash);
    }

    private String getFileName(String fullPath){
        int lastSlash = fullPath.lastIndexOf("/");
        int lastDot = fullPath.lastIndexOf(".");
        return fullPath.substring(lastSlash,lastDot);
    }

    //TODO: set speed, etc.
    public void run(GameBoyOptions options) throws Exception {
        double remainingCyclesPerFrame = Const.CYCLES_PER_FRAME;
        long nanosStartFrame = System.nanoTime();

        while (options.getCycles() == -1 || totalCycles < options.getCycles()) {

            if(!paused){
                int instrCycles = controlUnit.runInstruction();
                controlUnit.checkInterruptions();
                timer.tick(instrCycles);
                gpu.tick(instrCycles);

                remainingCyclesPerFrame -= instrCycles;

                if (remainingCyclesPerFrame <= 0) {
                    callFrameFinishedListeners();
                    if(options.getSpeed()!=-1){
                        long nanosEndFrame = System.nanoTime();
                        long elapsedTimeNanos = (nanosEndFrame - nanosStartFrame);
                        long remainingTimeNanos = Const.NANOS_PER_FRAME - elapsedTimeNanos;

                        if (remainingTimeNanos > 0) {
                            long millisToSleep = remainingTimeNanos / 1000000;
                            int nanosToSleep = (int) (remainingTimeNanos - millisToSleep * 1000000);
                            Thread.sleep(millisToSleep, nanosToSleep);
                        }
                        nanosStartFrame = System.nanoTime();
                    }
                    remainingCyclesPerFrame = Const.CYCLES_PER_FRAME;

                }
                totalCycles += instrCycles;
            }

        }
    }

    public void togglePause(){
        paused = !paused;
    }

    public Memory getMemory() {
        return memory;
    }

    public Registers getRegisters() {
        return registers;
    }

    public Timer getTimer() {
        return timer;
    }

    public Gpu getGpu() {
        return gpu;
    }

    public Cartridge getCartridge() {
        return cartridge;
    }

    public InputState getInputState() {
        return inputDevice.getInputState();
    }

    public void addFrameFinishedListener(GbListener listener) {
        if (listener instanceof FrameFinishedListener) {
            frameFinishedListeners.add((FrameFinishedListener) listener);
        }
    }

    public void setDebuggerController(DebuggerController controller) {
        this.controlUnit.setDebuggerController(controller);
    }


    private void callFrameFinishedListeners() {
        for (FrameFinishedListener listener : frameFinishedListeners) {
            listener.frameFinished(this);
        }
    }

    public long getTotalCycles() {
        return totalCycles;
    }

    public Lcd getLcd() {
        return lcd;
    }

    public synchronized void toggleBackground(){
        gpu.setBackgroundOn(!gpu.isBackgroundOn());
    }

    public synchronized void toggleWindow(){
        gpu.setWindowOn(!gpu.isWindowOn());
    }

    public synchronized void toggleSprites(){
        gpu.setSpritesOn(!gpu.areSpritesOn());
    }

    public ControlUnit getControlUnit() {
        return controlUnit;
    }
}
