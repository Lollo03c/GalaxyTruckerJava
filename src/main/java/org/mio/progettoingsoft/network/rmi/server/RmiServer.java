package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.GameController;
import org.mio.progettoingsoft.network.ServerController;
import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    final static Logger logger = LoggerFactory.getLogger(RmiServer.class);

    final List<VirtualClient> clients = new ArrayList<>();

    final GameController gameController;
    final ServerController serverController;

    public RmiServer() throws RemoteException {
        super();
        this.gameController = new GameController();
        this.serverController = new ServerController();
    }

    @Override
    public void connect(VirtualClient client) throws RemoteException {
        synchronized (this.clients) {
            this.clients.add(client);

            logger.info("Client {} has connected to server", client);

            /*
            * Dovremmo aggiungere una queue per gestire l'accesso di più client e gestirli sequenzialmente in modo da
            * creare solo le partite realmente necessarie.
            */
            // Player is ready to join a game
            serverController.addPlayerToGame(client, "prova");
        }
    }

    @Override
    public void sendToServer(Message message) throws RemoteException {
        serverController.handleInput(message);
    }
}
