package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.GameState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClient extends Remote{
    void setState(GameState gameState) throws RemoteException;
}
