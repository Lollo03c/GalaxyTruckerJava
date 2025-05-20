package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.network.client.VirtualClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends VirtualServer, Remote {
    int registerClient(VirtualClient client) throws RemoteException;
}
