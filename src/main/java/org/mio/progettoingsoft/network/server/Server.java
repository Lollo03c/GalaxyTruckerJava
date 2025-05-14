package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.model.enums.GameInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class Server extends UnicastRemoteObject implements VirtualServer {

    public Server() throws RemoteException {
        super();
    }

    public abstract void run();

}
