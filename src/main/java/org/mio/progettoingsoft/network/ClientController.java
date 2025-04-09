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
            case RequestSetupMessage rsm -> {
                System.out.print("Non ci sono partite disponibili. Creazione di partita in corso...\n" +
                        "Insersci un numero di giocatori: ");
                Scanner scan = new Scanner(System.in);
                server.sendInput(new GameSetupInput(message.getClient(), message.getNickname(), scan.nextInt()));
            }
            case JoinedGameMessage jgm -> {
                System.out.print("Ti sei unito ad una partita.");
            }
            default -> System.err.println("Messaggio non gestito: " + message);
        }
    }
}
