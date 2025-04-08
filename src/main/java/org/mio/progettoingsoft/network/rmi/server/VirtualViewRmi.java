package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.network.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewRmi extends Remote, VirtualView {
    @Override
    void showUpdate(Integer number) throws RemoteException;

    @Override
    void reportError(String details) throws RemoteException;

    @Override
    void requestGameSetup() throws RemoteException;

    @Override
    void requestNickname() throws RemoteException;

    @Override
    void notify(String message) throws RemoteException;
}
