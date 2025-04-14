package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.network.VirtualView;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewRmi extends Remote, VirtualView {
    @Override
    void update(Message message) throws RemoteException;

    @Override
    void reportError(String details) throws RemoteException;


    // DA ELIMINARE
    @Override
    void notify(String message) throws RemoteException;
}
