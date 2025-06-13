package org.mio.progettoingsoft.network.server.rmi;


import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.advCards.sealed.SldStardust;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    private final ServerController controller;
    private final ConnectionInfo connectionInfo;
    private ExecutorService executors = Executors.newFixedThreadPool(1);

    public RmiServer(ConnectionInfo connectionInfo) throws RemoteException {
        super(); // automatic export of UnicastRemoteObject
        this.controller = ServerController.getInstance();
        this.connectionInfo = connectionInfo;
    }

    public void startServer() {
        try {
            System.setProperty("java.rmi.server.hostname", connectionInfo.getIpHost());

            Registry registry = LocateRegistry.createRegistry(connectionInfo.getRmiPort());
            registry.rebind(connectionInfo.getServerName(), this);

            Logger.info("SERVER RMI STARTED | Port: " + connectionInfo.getRmiPort() + " | IP: " + connectionInfo.getIpHost() + " | Server: " + connectionInfo.getServerName());

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
        executors.submit(() -> {
            controller.handleNickname(tempIdClient, nickname);
        });
    }

    @Override
    public void handleGameInfo(GameInfo gameInfo, String nickname) throws RemoteException {
        executors.submit(() -> {
            controller.handleGameInfo(gameInfo, nickname);
        });
    }

    @Override
    public void getCoveredComponent(int idGame, String nickname) throws RemoteException {
        executors.submit(() -> {
            controller.getCoveredComponent(idGame, nickname);
        });
    }

    @Override
    public void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException {
        executors.submit(() -> {
            controller.addComponent(idGame, nickname, idComp, cordinate, rotations);
        });
    }

    @Override
    public void discardComponent(int idGame, int idComponent) throws RemoteException {
        executors.submit(() -> {
            controller.discardComponent(idGame, idComponent);
        });
    }

    @Override
    public void drawUncovered(int idGame, String nickname, int idComponent) throws RemoteException {
        executors.submit(() -> {
            controller.drawUncovered(idGame, nickname, idComponent);
        });
    }

    @Override
    public void bookDeck(int idGame, String nickname, int deckNumber) throws RemoteException {
        executors.submit(() -> {
            controller.bookDeck(idGame, nickname, deckNumber);
        });
    }

    @Override
    public void freeDeck(int idGame, String nickname, int deckNumber) throws RemoteException {
        executors.submit(() -> {
            controller.freeDeck(idGame, nickname, deckNumber);
        });
    }

    @Override
    public void takeBuild(int idGame, String nickname) throws RemoteException{
        executors.submit(() ->
            controller.takeBuild(idGame, nickname)
        );
    }

    @Override
    public void endBuild(int idGame, String nickname) throws RemoteException {
        executors.submit(() -> {
            controller.endBuild(idGame, nickname);
        });
    }

    @Override
    public void choosePlace(int idGame, String nickname, int place) throws RemoteException {
        executors.submit(() -> {
            controller.choosePlace(idGame, nickname, place);
        });
    }

    @Override
    public void applyStardust(int idGame, String nickname, SldStardust card) throws RemoteException {
        executors.submit(() -> {
            controller.applyStardust(idGame, card);
        });
    }

    @Override
    public void activateDoubleEngine(int idGame, String nickname, int number) throws RemoteException {
        executors.submit(() -> {
            controller.activateDoubleEngine(idGame, nickname, number);
        });
    }

    @Override
    public void leaveFlight(int idGame, String nickname) throws RemoteException {
        executors.submit(() -> {
            controller.leaveFlight(idGame, nickname);
        });
    }

    @Override
    public void drawCard(int idGame, String nickname) throws RemoteException {
        executors.submit(() -> {
            controller.drawCard(idGame, nickname);
        });
    }

    @Override
    public void skipEffect(int idGame, String nickname, int idCard) throws RemoteException{
        executors.submit(() ->
                controller.skipEffect(idGame, nickname, idCard)
        );
    }

    @Override
    public void crewRemove(int idGame, String nickname, List<Cordinate> cordsToRemove) throws RemoteException{
        executors.submit(() ->
            controller.removeCrew(idGame, nickname, cordsToRemove)
        );
    }
}
