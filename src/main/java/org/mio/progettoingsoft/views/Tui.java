package org.mio.progettoingsoft.views;

import org.mio.progettoingsoft.network.ConnectionType;

import java.util.Scanner;

public class Tui implements VirtualView {
    private final Scanner scan;

    public Tui() {
        scan = new Scanner(System.in);
    }

    @Override
    public ConnectionType askConnectionType() {
        System.out.println("Select connection type: ");
        System.out.println("1: RMI");
        System.out.println("2: Socket");
        System.out.print("Make your choice: ");
        boolean isRmi = (scan.nextInt() == 1);

        ConnectionType connectionType = new ConnectionType(isRmi, "127.0.0.1", 1234, "localhost");

        return connectionType;
    }

    @Override
    public String askNickname() {
        System.out.print("To be able to connect to the server, enter a nickname: ");
        return scan.nextLine();
    }
}
