package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.VirtualServer;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {
    void connect(VirtualClient client, String nickname) throws RemoteException;

    @Override
    void sendInput(Message message) throws RemoteException;
}
