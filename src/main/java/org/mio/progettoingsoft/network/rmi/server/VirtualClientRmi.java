package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClientRmi extends Remote, VirtualClient {
    @Override
    void sendToClient(Message message) throws RemoteException;

    @Override
    void reportError(String details) throws RemoteException;
}
