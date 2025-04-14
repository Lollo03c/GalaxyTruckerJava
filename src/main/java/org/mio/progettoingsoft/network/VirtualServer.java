package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.server.VirtualViewRmi;

import java.rmi.RemoteException;

public interface VirtualServer {
    public void sendInput(Message message) throws RemoteException;
}
