package org.mio.progettoingsoft.views;

import org.mio.progettoingsoft.GameState;

import java.util.Scanner;

public class Tui implements VirtualView {
    private final Scanner scan;

    public Tui() {
        scan = new Scanner(System.in);
    }

    public String gameMenu(GameState gameState) {
        return switch (gameState) {
            case START -> printConnectionType();
            case NICKNAME_REQUEST -> printNicknameRequest();
            default -> "Invalid gameState";
        };
    }

    private String printConnectionType() {
        System.out.println("Select connection type: ");
        System.out.println("1: RMI");
        System.out.println("2: Socket");
        System.out.print("Make your choice: ");
        return scan.nextLine();
    }

    private String printNicknameRequest() {
        System.out.println("Select nickname: ");
        return scan.nextLine();
    }
}
