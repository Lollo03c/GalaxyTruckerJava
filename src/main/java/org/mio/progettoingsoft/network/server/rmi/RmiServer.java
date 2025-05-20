package org.mio.progettoingsoft.network.server.rmi;


import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.Logger;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer implements VirtualServerRmi {
    private final ServerController controller;
    private final ConnectionInfo connectionInfo;

    public RmiServer(ConnectionInfo connectionInfo) {
        controller = ServerController.getInstance();
        this.connectionInfo = connectionInfo;

        try {
            UnicastRemoteObject.exportObject(this, connectionInfo.rmiPort());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer() {
        final int portRmi = connectionInfo.rmiPort();
        try {
            //192.168.1.147
            System.setProperty("java.rmi.server.hostname", connectionInfo.ip());
            Registry registry =  LocateRegistry.createRegistry(portRmi);

            registry.rebind(connectionInfo.serverName(), this);

            Logger.info("Server RMI running on port " + portRmi);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * METHODS INHERITED FROM VirtualServerRmi INTERFACE, they're directly called from the client
     */

    @Override
    public int registerClient(VirtualClient client) throws RemoteException {
        return controller.addClientToAccept(client);
    }

    @Override
    public void handleNickname(int tempIdClient, String nickname) throws RemoteException {
        controller.handleNickname(tempIdClient, nickname);
    }

    @Override
    public void handleGameInfo(GameInfo gameInfo, String nickname) throws RemoteException {
        controller.handleGameInfo(gameInfo, nickname);
    }

    @Override
    public void getCoveredComponent(int idGame, String nickname) throws RemoteException {
        controller.getCoveredComponent(idGame, nickname);
    }

    @Override
    public void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException {
        controller.addComponent(idGame, nickname, idComp, cordinate, rotations);
    }

    @Override
    public void discardComponent(int idGame, int idComponent) throws RemoteException {
        controller.discardComponent(idGame, idComponent);
    }

    @Override
    public void drawUncovered(int idGame, String nickname, int idComponent) throws RemoteException {
        controller.drawUncovered(idGame, nickname, idComponent);
    }

    @Override
    public void bookDeck(int idGame, String nickname, int deckNumber) throws RemoteException {
        controller.bookDeck(idGame, nickname, deckNumber);
    }

    @Override
    public void freeDeck(int idGame, String nickname, int deckNumber) throws RemoteException {
        controller.freeDeck(idGame, nickname, deckNumber);
    }
}
