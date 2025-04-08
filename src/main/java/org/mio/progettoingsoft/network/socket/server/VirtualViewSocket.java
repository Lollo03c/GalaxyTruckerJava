package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.network.VirtualView;

import java.rmi.RemoteException;

/**
 * Questa interfaccia specializza l'interfaccia VirtualView per la tecnologia Socket
 */
public interface VirtualViewSocket extends VirtualView {
    @Override
    public void showUpdate(Integer number);
    @Override
    public void reportError(String details) throws RemoteException;
}
