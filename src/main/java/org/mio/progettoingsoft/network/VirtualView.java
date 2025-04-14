package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;

import java.rmi.RemoteException;

public interface VirtualView {
    public void update(Message message) throws RemoteException;
    public void reportError(String details) throws RemoteException;


    // DA ELIMINARE
    public void notify(String message) throws RemoteException;
}
