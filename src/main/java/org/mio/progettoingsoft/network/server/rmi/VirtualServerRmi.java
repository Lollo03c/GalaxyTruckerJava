package org.mio.progettoingsoft.network.server.rmi;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.rmi.RemoteException;

/**
 * Extends the {@link VirtualServer} interface to include RMI-specific methods.
 * This interface defines the contract for an RMI-enabled server, allowing clients
 * to register themselves to receive callbacks.
 */
public interface VirtualServerRmi extends VirtualServer {
    int registerClient(VirtualClient client) throws RemoteException;
}
