package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class Client extends UnicastRemoteObject implements VirtualClient, Serializable {
    protected Client() throws RemoteException {
        super();
    }

    VirtualServer virtualServer;
    protected ClientController controller = ClientController.getInstance();

    public abstract void connect();
    public abstract void registryClient();

    public abstract void setNickname(String nickname) throws IncorrectNameException, IncorrectClientException, SetGameModeException;
    public abstract void setGameInfo(GameInfo gameInfo);

}
