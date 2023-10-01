package com.ismaelrh.gameboy;

import com.ismaelrh.gameboy.cpu.cartridge.Cartridge;
import com.ismaelrh.gameboy.debug.FpsInfo;
import com.ismaelrh.gameboy.debug.debugger.console.ConsoleController;
import com.ismaelrh.gameboy.debug.tileset.TileSetDisplay;
import com.ismaelrh.gameboy.gpu.lcd.swing.SwingLcd;
import com.ismaelrh.gameboy.input.InputState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DevGui {

    private static final Logger log = LogManager.getLogger(DevGui.class);

    public static void main(String[] args) throws Exception {

        SwingLcd lcd = new SwingLcd(2);

        GameBoy gameBoy = new GameBoy(lcd);
        gameBoy.loadCartridge("/Users/ismaelrh/gb/mbc1/multicart_rom_8Mb.gb");

        TileSetDisplay displayTileset0 = new TileSetDisplay((char) 0x8000);
        TileSetDisplay displayTileset1 = new TileSetDisplay((char) 0x8800);

        gameBoy.addFrameFinishedListener(displayTileset0);
        gameBoy.addFrameFinishedListener(displayTileset1);

        gameBoy.setDebuggerController(new ConsoleController());
        //gameBoy.setBootrom("/Users/ismaelrh/gb/dmg_boot.bin");

        startGUI(gameBoy, lcd.getDisplayPanel(), displayTileset0.getDisplayPanel(), displayTileset1.getDisplayPanel());
        gameBoy.run(new GameBoyOptions(250_000_000,-1));
        System.out.println("Finished execution. Cycles = " + gameBoy.getTotalCycles() +  " LCD md5=" + lcd.getHash());
    }

    private static JFrame startGUI(GameBoy gameBoy, JPanel display, JPanel tileset0, JPanel tileset1) {
        JFrame window = new JFrame("gameboy4j v0.1");
        gameBoy.addFrameFinishedListener(new FpsInfo(window));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        window.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                setKey(gameBoy.getInputState(), e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                setKey(gameBoy.getInputState(), e.getKeyCode(), false);
            }
        });

        JLabel coordinates = new JLabel("Coords");

        display.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                coordinates.setText("coords=[" + e.getX() / 2 + "," + e.getY() / 2 + "], tile=[" + 1 + "]");
            }
        });
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //window.setContentPane(display);
        mainPanel.add(new JLabel(generateTitle(gameBoy.getCartridge())));
        mainPanel.add(new JLabel(generateDetails(gameBoy.getCartridge())));
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

    private static void setKey(InputState inputState, int keyCode, boolean state) {
        switch (keyCode) {
            case 87:
                inputState.setUp(state);
                break;
            case 65:
                inputState.setLeft(state);
                break;
            case 83:
                inputState.setDown(state);
                break;
            case 68:
                inputState.setRight(state);
                break;
            case 74:
                inputState.setB(state);
                break;
            case 75:
                inputState.setA(state);
                break;
            case 8:
                inputState.setSelect(state);
                break;
            case 10:
                inputState.setStart(state);
                break;
            default:
                break;
        }
    }

    private static String generateTitle(Cartridge cartridge) {
        if(cartridge==null){
            return "NO CARTRIDGE";
        }
        return cartridge.getTitle() + " - " + cartridge.getCartridgeType();
    }

    private static String generateDetails(Cartridge cartridge) {
        if(cartridge==null){
            return "";
        }
        return "ROM: " + cartridge.getRomSizeBytes() / 1024 + "KB, RAM: " + cartridge.getRamSizeBytes() / 1024 + "KB";
    }
}
