package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.SerMessage.GameSetupInput2;
import org.mio.progettoingsoft.network.SerMessage.SerMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.server.VirtualViewRmi;

import java.rmi.RemoteException;

public interface VirtualServer {
    public void sendInput(Message message) throws RemoteException;

    public void sendInput2(SerMessage gameSetupInput2) throws RemoteException;
}
