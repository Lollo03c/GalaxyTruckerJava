package org.mio.progettoingsoft.network;

import java.rmi.RemoteException;

public interface Client {
    public void run() throws RemoteException;
    public void close();
}
