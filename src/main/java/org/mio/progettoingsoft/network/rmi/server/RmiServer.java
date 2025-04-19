package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.GameController;
import org.mio.progettoingsoft.network.ServerController;
import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    final Map<VirtualClient, String> clients = new HashMap<>();

    final GameController gameController;
    final ServerController serverController;

    public RmiServer() throws RemoteException {
        super();
        this.gameController = new GameController();
        this.serverController = new ServerController();
    }

    @Override
    public void connect(VirtualClient client) throws RemoteException {
        String nickname = "prova";

        synchronized (this.clients) {
            this.clients.put(client, nickname);

            System.out.println(nickname + " connected.");

            /*
            * Dovremmo aggiungere una queue per gestire l'accesso di più client e gestirli sequenzialmente in modo da
            * creare solo le partite realmente necessarie.
            */
            // Player is ready to join a game
            serverController.addPlayerToGame(client, nickname);
        }
    }

    @Override
    public void sendToServer(Message message) throws RemoteException {
        serverController.handleInput(message);
    }
}
