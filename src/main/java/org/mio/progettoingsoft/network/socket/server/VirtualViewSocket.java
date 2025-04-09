package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.network.VirtualView;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.RemoteException;

/**
 * Questa interfaccia specializza l'interfaccia VirtualView per la tecnologia Socket
 */
public interface VirtualViewSocket extends VirtualView {
    @Override
    public void update(Message message);
    @Override
    public void reportError(String details) throws RemoteException;
}
