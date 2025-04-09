package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.Controller;
import org.mio.progettoingsoft.Lobby;
import org.mio.progettoingsoft.network.VirtualView;
import org.mio.progettoingsoft.network.message.GameSetupInput;
import org.mio.progettoingsoft.network.message.JoinedGameMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.RequestSetupMessage;
import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    final Map<VirtualViewRmi, String> clients = new HashMap<>();

    final Controller controller;
    final Lobby lobby;

    public RmiServer() throws RemoteException {
        super();
        this.controller = new Controller();
        this.lobby = new Lobby();
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
            client.notify("Connesso al server.");

            System.out.println("Client connesso: " + nickname);

            for (VirtualView c : clients.keySet()) {
                if (c != client) {
                    c.notify(nickname + " si è connesso al server.");
                }
            }

            /*
            * Dovremmo aggiungere una queue per gestire l'accesso di più client e gestirli sequenzialmente in modo da
            * creare solo le partite realmente necessarie.
            */
            // Player is ready to join a game
            if(lobby.getWaitingGame() == null) {
                client.update(new RequestSetupMessage(client, nickname));
            } else {
                lobby.joinGame(client, nickname);
                client.update(new JoinedGameMessage(client, nickname));
            }
        }
    }

    @Override
    public void sendInput(Message message) throws RemoteException {
        switch (message) {
            case GameSetupInput gsi -> {
                GameSetupInput input = (GameSetupInput) message;
                lobby.createGame(input.getClient(), input.getNickname(), input.getNumPlayers());

                System.out.print("Partita creata da " + input.getNickname() + "\n");
            }
            default -> throw new RemoteException();
        }
    }

    @Override
    public void createGame(VirtualView client, String nickname, int numPlayers) throws RemoteException {
        lobby.createGame(client, nickname, numPlayers);
    }

    @Override
    public void joinGame(VirtualView client, String nickname) throws RemoteException {
        lobby.joinGame(client, nickname);
    }
}
