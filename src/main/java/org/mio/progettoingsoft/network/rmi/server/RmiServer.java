package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.Controller;
import org.mio.progettoingsoft.Lobby;
import org.mio.progettoingsoft.network.VirtualView;
import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    final List<VirtualViewRmi> clients = new ArrayList<>();

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
    public void connect(VirtualViewRmi client) throws RemoteException {
        synchronized (this.clients) {
            this.clients.add(client);
        }
    }

    @Override
    public void join(String nickname, VirtualView client) throws RemoteException {
        if(lobby.checkAvailableGames().isPresent()) {
            lobby.createGame(nickname, 4);
        } else {
            this.controller.addPlayer(.getFirst(), nickname);
        }
    }
}
