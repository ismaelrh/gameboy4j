package com.ismaelrh.gameboy.debug.debugger.console;

import java.util.Scanner;

/**
 * Thread that listens for debugging commands without blocking and then executes it, calling the debugger.
 */
class ConsoleListenerThread implements Runnable {

    private final ConsoleController controller;
    private final Scanner scanner;

    public ConsoleListenerThread(ConsoleController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            nextCommand();
        }
    }

    private void nextCommand() {
        System.out.print(">");
        String input = scanner.nextLine();
        System.out.print("\n");
        try {
            controller.parseCommand(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
