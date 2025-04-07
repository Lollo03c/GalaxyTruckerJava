package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.VirtualServer;
import org.mio.progettoingsoft.network.VirtualView;
import org.mio.progettoingsoft.network.rmi.server.VirtualViewRmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {
    void connect(VirtualViewRmi client) throws RemoteException;

    @Override
    public void join(String nickname , VirtualView client ) throws RemoteException;
}
