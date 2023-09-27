package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.Const;
import com.ismaelrh.gameboy.cpu.ControlUnit;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import com.ismaelrh.gameboy.cpu.cartridge.CartridgeFactory;
import com.ismaelrh.gameboy.cpu.memory.Memory;
import com.ismaelrh.gameboy.cpu.periphericals.timer.Timer;
import com.ismaelrh.gameboy.debug.blargg.BlarggTestInterceptor;
import com.ismaelrh.gameboy.debug.debugger.console.ConsoleController;
import com.ismaelrh.gameboy.debug.logCheck.binjgb.BinJgbLogStatusProvider;
import com.ismaelrh.gameboy.debug.tileset.TileSetDisplay;
import com.ismaelrh.gameboy.gpu.Gpu;
import com.ismaelrh.gameboy.gpu.lcd.swing.SwingLcd;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameBoyDebugger {

    private static final Logger log = LogManager.getLogger(GameBoyDebugger.class);

    public static void main(String[] args) throws Exception {

        Memory memory = new Memory();
        memory.write((char)0xFF40,(byte)0x91);
        byte a = memory.read((char)0xFF40);
        Registers registers = new Registers();
        registers.initForRealGB();
        Timer timer = new Timer(memory);

        SwingLcd lcd = new SwingLcd(2);

        Gpu gpu = new Gpu(memory, lcd);
        TileSetDisplay displayTileset0 = new TileSetDisplay(memory, gpu, (char) 0x8000);
        TileSetDisplay displayTileset1 = new TileSetDisplay(memory, gpu, (char) 0x8800);

        ControlUnit controlUnit = new ControlUnit(registers, memory);

        //Register console debugger
        controlUnit.setDebuggerController(new ConsoleController());

        //Log status provider
        //controlUnit.setLogStatusProvider(new BinJgbLogStatusProvider("/Users/ismaelrh/gb/log.txt"));

        //Register blargg interceptor to get output and put it on console
        memory.addInterceptor(new BlarggTestInterceptor());
        memory.addMMIODevice(timer);
        memory.addMMIODevice(gpu);

        //Cartridge cartridge = new BasicCartridge("Blargg CPU test 6", "/Users/ismaelrh/gb/dr_mario.gb");
        Cartridge cartridge = CartridgeFactory.create("/Users/ismaelrh/gb/dr_mario.gb");
        memory.insertCartridge(cartridge);
        //setBootrom(memory, registers, "/Users/ismaelrh/gb/dmg_boot.bin");


        startGUI(cartridge,lcd.getDisplayPanel(), displayTileset0.getDisplayPanel(), displayTileset1.getDisplayPanel());

        double remainingCyclesPerFrame = Const.CYCLES_PER_FRAME;
        long nanosStartFrame = System.nanoTime();


        //I end on the end of every frame to check
        long totalC = 0;
        long startTime = System.currentTimeMillis();
        while (true) {

            int cycles = controlUnit.runInstruction();
            controlUnit.checkInterruptions();
            timer.tick(cycles);
            gpu.tick(cycles);

            remainingCyclesPerFrame -= cycles;

            if (remainingCyclesPerFrame <= 0) {
                displayTileset0.display();
                displayTileset1.display();
                long nanosEndFrame = System.nanoTime();
                long elapsedTimeNanos = (nanosEndFrame - nanosStartFrame);
                long remainingTimeNanos = Const.NANOS_PER_FRAME - elapsedTimeNanos;

                if (remainingTimeNanos > 0) {
                    long millisToSleep = remainingTimeNanos / 1000000;
                    int nanosToSleep = (int) (remainingTimeNanos - millisToSleep * 1000000);
                    Thread.sleep(millisToSleep, nanosToSleep);
                }
                nanosStartFrame = System.nanoTime();
                remainingCyclesPerFrame = Const.CYCLES_PER_FRAME;

            }

            totalC += cycles;
            /*if (totalC % 1000000 == 0) {
                double totalTime = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.println("Pace: " + totalC / (totalTime * Const.CYCLES_PER_FRAME) + " frames/s");
            }*/


        }
        //blargg.flush();
    }

    private static void setBootrom(Memory memory, Registers registers, String filePath) throws Exception {
        memory.setBootrom(filePath);
        registers.setPC((char) 0x0000);
    }

    private static JFrame startGUI(Cartridge cartridge,JPanel display, JPanel tileset0, JPanel tileset1) {
        JFrame window = new JFrame("gameboy4j v0.1");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        JLabel coordinates = new JLabel("Coords");

        display.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
               coordinates.setText("coords=[" + e.getX()/2 + "," +e.getY()/2 +"], tile=[" + 1 + "]");
            }
        });
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //window.setContentPane(display);
        mainPanel.add(new JLabel(generateTitle(cartridge)));
        mainPanel.add(new JLabel(generateDetails(cartridge)));
        mainPanel.add(display);
        mainPanel.add(coordinates);


        JPanel tilesetPanel = new JPanel();
        tilesetPanel.setLayout(new BoxLayout(tilesetPanel, BoxLayout.X_AXIS));

        JPanel tilesetPanel0 = new JPanel();
        tilesetPanel0.setLayout(new BoxLayout(tilesetPanel0, BoxLayout.Y_AXIS));
        tilesetPanel0.add(new JLabel("Tile set 0"));
        tilesetPanel0.add(tileset0);

        JPanel tilesetPanel1 = new JPanel();
        tilesetPanel1.setLayout(new BoxLayout(tilesetPanel1, BoxLayout.Y_AXIS));
        tilesetPanel1.add(new JLabel("Tile set 1"));
        tilesetPanel1.add(tileset1);

        tilesetPanel.add(tilesetPanel0);
        tilesetPanel.add(tilesetPanel1);
        mainPanel.add(tilesetPanel);
        window.setContentPane(mainPanel);
        window.setResizable(false);
        window.setVisible(true);
        window.pack();
        return window;
    }

    private static String generateTitle(Cartridge cartridge){
        return cartridge.getTitle() + " - " + cartridge.getCartridgeType();
    }
    private static String generateDetails(Cartridge cartridge){
        return "ROM: " + cartridge.getRomSizeBytes()/1024 + "KB, RAM: " + cartridge.getRamSizeBytes()/1024 + "KB";
    }
}
