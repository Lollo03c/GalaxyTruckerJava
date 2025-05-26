package org.mio.progettoingsoft.network.client.rmi;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.VirtualServer;
import org.mio.progettoingsoft.network.server.rmi.VirtualServerRmi;
import org.mio.progettoingsoft.utils.ConnectionInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RmiClient implements VirtualClient, Client {
    private final VirtualServerRmi server;
    private ExecutorService executors = Executors.newFixedThreadPool(4);
    private final ClientController controller;
    private final ConnectionInfo connectionInfo;

    public RmiClient(ConnectionInfo connectionInfo) throws RemoteException {
        controller = ClientController.getInstance();
        this.connectionInfo = connectionInfo;

        try {
            System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        UnicastRemoteObject.exportObject(this, 0);

        try {
            Registry registry = LocateRegistry.getRegistry(connectionInfo.ip(), connectionInfo.rmiPort());
            server = (VirtualServerRmi) registry.lookup(connectionInfo.serverName());
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * METHODS INHERITED FROM Client INTERFACE
     */

    @Override
    public void connect() throws RemoteException {
        controller.setIdClient(server.registerClient(this));
    }

    @Override
    public VirtualServer getServer(){
        return server;
    }


    /**
     * METHODS INHERITED FROM VirtualClientRmi  INTERFACE, they're directly called from the server
     */
    @Override
    public void ping(String msg) throws RemoteException{
        System.out.println(msg);
    }

    @Override
    public void setNickname(String nickname) throws RemoteException{
        executors.submit(() -> {
            controller.setNickname(nickname);
            System.out.println("nickname corretto");
            controller.setState(GameState.WAITING);
        });
    }

    @Override
    public void askGameSettings(String nickname) throws RemoteException{
        executors.submit(() -> {
            controller.setState(GameState.GAME_MODE);
        });
    }

    @Override
    public void wrongNickname() throws RemoteException{
        executors.submit(() -> {
            controller.setState(GameState.ERROR_NICKNAME);
        });
    }

    @Override
    public void setGameId(int idGame) {
        controller.setGameId(idGame);
    }

    @Override
    public void setState(GameState state) throws RemoteException{
        controller.setState(state);
    }

    @Override
    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players) throws RemoteException{
        executors.submit(() ->
                controller.setFlyBoard(mode, players)
        );
    }

    @Override
    public void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException{
        executors.submit(() ->
                controller.addOtherPlayersComponent(nickname, idComp, cordinate, rotations)
        );
    }

    @Override
    public void setInHandComponent(int idComponent) throws RemoteException{
        executors.submit(() ->
                controller.setInHandComponent(idComponent)
        );
    }

    @Override
    public void addUncoveredComponent(int idComp) throws RemoteException{
        executors.submit(() -> {
            controller.addUncoveredComponent(idComp);
        });
    }

    @Override
    public void removeUncovered(int idComp) throws RemoteException{
        executors.submit(() ->
                controller.removeUncovered(idComp)
        );
    }

    @Override
    public void removeDeck(Integer deckNumber) throws RemoteException{
        executors.submit(() -> controller.removeDeck(deckNumber));
    }

    @Override
    public void setInHandDeck(int deck){
        executors.submit(() -> controller.setInHandDeck(deck));
    }

    @Override
    public void addAvailableDeck(int deckNumber){
        executors.submit(() -> controller.addAvailableDeck(deckNumber));
    }
}