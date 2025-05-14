package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;

public class ServerRmi extends Server implements VirtualServer, Serializable {


    public ServerRmi() throws RemoteException {
        super();
    }

    @Override
    public void run(){
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");

            Registry registry = LocateRegistry.createRegistry(1099);

            VirtualServer virtualServer = new ServerRmi();
            registry.rebind("GameSpace", virtualServer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int registerClient(VirtualClient client) throws RemoteException{
        GameManager gameManager = GameManager.getInstance();
        int idClient = gameManager.addClientToAccept(client);

        return idClient;
    }

    @Override
    public void setNickname(int clientId, String nickname) throws IncorrectNameException, IncorrectClientException, SetGameModeException {
        GameManager gameManager = GameManager.getInstance();

        gameManager.addPlayerToGame(clientId, nickname);
    }

    @Override
    public void setGameInfo(GameInfo gameInfo) {
        GameManager.getInstance().getWaitingGame().setupGame(gameInfo.mode(), gameInfo.nPlayers());
    }

}
