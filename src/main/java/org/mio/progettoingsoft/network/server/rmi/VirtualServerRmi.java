package org.mio.progettoingsoft.network.server.rmi;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends VirtualServer {
    int registerClient(VirtualClient client) throws RemoteException;
}
