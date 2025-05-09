package org.mio.progettoingsoft.network.client.rmi;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.VirtualServer;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {
    void connect(VirtualClient client) throws Exception;

    @Override
    void sendToServer(Message message) throws RemoteException;
}
