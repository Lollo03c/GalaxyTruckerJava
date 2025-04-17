package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.network.SerMessage.SerMessage;
import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.RemoteException;

/**
 * Questa interfaccia specializza l'interfaccia VirtualView per la tecnologia Socket
 */
public interface VirtualClientSocket extends VirtualClient {
    @Override
    public void update(Message message);
    @Override
    public void reportError(String details) throws RemoteException;
    public void update2(SerMessage message) throws RemoteException;
}
