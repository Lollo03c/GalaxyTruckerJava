package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerRMI extends Server implements VirtualServer {

    public ServerRMI() throws RemoteException{
        UnicastRemoteObject.exportObject(this, 1099);
    }

    @Override
    public int registerClient(VirtualClient client) throws RemoteException{
        GameManager gameManager = GameManager.getInstance();

        int idClient = gameManager.addClientToAccept(client);
        client.ping("ciao sono il server");
        return idClient;
    }



}
