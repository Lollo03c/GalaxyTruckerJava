package org.mio.progettoingsoft.network.server.rmi;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClientRmi extends Remote, VirtualClient {
    @Override
    void showUpdate(Message message) throws RemoteException;
}
