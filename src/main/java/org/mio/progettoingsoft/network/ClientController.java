package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.GameSetupInput;
import org.mio.progettoingsoft.network.message.JoinedGameMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.RequestSetupMessage;

import java.rmi.RemoteException;
import java.util.Scanner;

public class ClientController {
    VirtualServer server;

    public ClientController(VirtualServer server) {
        this.server = server;
    }

    public void handleMessage(Message message) throws RemoteException {
        switch (message) {
            case RequestSetupMessage rsm -> handleGameSetup(message);
            case JoinedGameMessage jgm -> {}
            default -> System.err.println("Unhandle message: " + message);
        }
    }

    private void handleGameSetup(Message message) throws RemoteException {
        Scanner scan = new Scanner(System.in);

        System.out.print("No match available. Creating a match...\n");

        int numPlayers;
        do {
            System.out.print("Enter the number of players: ");
            numPlayers = scan.nextInt();
        } while (numPlayers < 1 || numPlayers > 4);

        server.sendInput(new GameSetupInput(message.getClient(), message.getNickname(), numPlayers));
    }
}
