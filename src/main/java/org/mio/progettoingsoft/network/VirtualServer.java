package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.server.VirtualViewRmi;

import java.rmi.RemoteException;

public interface VirtualServer {
    public void sendInput(Message message) throws RemoteException;

    // DA ELIMINARE
    public void createGame(VirtualView client, String nickname , int numPlayers) throws RemoteException;
    public void joinGame(VirtualView client, String nickname) throws RemoteException;
}
