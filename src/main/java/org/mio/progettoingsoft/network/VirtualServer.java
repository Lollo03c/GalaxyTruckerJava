package org.mio.progettoingsoft.network;

import java.rmi.RemoteException;

public interface VirtualServer {
    public void join(String nickname , VirtualView client ) throws RemoteException;
}
