package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.VirtualServer;
import org.mio.progettoingsoft.network.VirtualView;
import org.mio.progettoingsoft.network.rmi.server.VirtualViewRmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {
    void connect(VirtualViewRmi client) throws RemoteException;

    @Override
    public void createGame(VirtualView client, String nickname, int numPlayers) throws RemoteException;

    @Override
    void joinGame(VirtualView client, String nickname) throws RemoteException;

    @Override
    void join(VirtualViewRmi client) throws RemoteException;
}
