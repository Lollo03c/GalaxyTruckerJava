package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.GameController;
import org.mio.progettoingsoft.network.SerMessage.GameSetupInput2;
import org.mio.progettoingsoft.network.SerMessage.SerMessage;
import org.mio.progettoingsoft.network.ServerController;
import org.mio.progettoingsoft.network.VirtualView;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    final Map<VirtualViewRmi, String> clients = new HashMap<>();

    final GameController gameController;
    final ServerController serverController;

    public RmiServer() throws RemoteException {
        super();
        this.gameController = new GameController();
        this.serverController = new ServerController();
    }

    public static void main(String[] args) throws RemoteException {
        final String serverName = "localhost";
        final int serverPort = 1234;

        VirtualServerRmi server = new RmiServer();

        Registry registry = LocateRegistry.createRegistry(serverPort);

        registry.rebind(serverName, server);

        System.out.println("Server started on port " + serverPort);
    }

    @Override
    public void connect(VirtualViewRmi client, String nickname) throws RemoteException {
        synchronized (this.clients) {
            this.clients.put(client, nickname);

            // Debugging purpose
            client.notify("Connected to the server.");

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
    public void sendInput(Message message) throws RemoteException {
        serverController.handleInput(message);
    }

    @Override
    public void sendInput2(SerMessage gameSetupInput2) throws RemoteException{

    }

}
