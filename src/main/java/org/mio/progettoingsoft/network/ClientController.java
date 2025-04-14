package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.SerMessage.*;
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
    public void handleMessage2(VirtualView client , SerMessage message) throws RemoteException {
        switch (message){
            case RequestSetupMessage2 npm -> handleGameSetup2(client , message);
            case JoinedGameMessage2 jgm2 -> {
                System.out.println(message.getNickname() + " joined game    ");
            }
            default-> System.out.println("Unhandle message: " + message);
        }
    }

    private void handleGameSetup2(VirtualView client , SerMessage message) throws RemoteException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("No match available , creating a match\n");
        int numPlayers ;
        do{
            System.out.println("enter number of players: ");
            numPlayers = scanner.nextInt();
        }while(numPlayers < 1 || numPlayers > 4);
        server.sendInput2(new GameSetupInput2(message.getNickname(), numPlayers));
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
