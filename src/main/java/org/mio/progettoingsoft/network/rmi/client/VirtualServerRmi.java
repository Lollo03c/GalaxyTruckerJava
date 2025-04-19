package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.VirtualServer;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {
    void connect(VirtualClient client) throws RemoteException;

    @Override
    void sendToServer(Message message) throws RemoteException;
}
