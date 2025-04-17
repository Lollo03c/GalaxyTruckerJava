package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.SerMessage.SerMessage;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.RemoteException;

public interface VirtualClient {
    //ho aggiunto update2 perchè update non utilizza messaggi serializable e quindi non posso usarlo per socket
    public void update(Message message) throws RemoteException;
    public void reportError(String details) throws RemoteException;

    public void update2(SerMessage message) throws RemoteException;

    public void notify(String message) throws RemoteException;
}
