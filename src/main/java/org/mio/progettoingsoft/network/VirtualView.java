package org.mio.progettoingsoft.network;

import java.rmi.RemoteException;

public interface VirtualView {
    public void showUpdate(Integer number) throws RemoteException;
    public void reportError(String details) throws RemoteException;
    
    public void requestGameSetup() throws RemoteException;
    public void requestNickname() throws RemoteException;
    public void notify(String message) throws RemoteException;

}
