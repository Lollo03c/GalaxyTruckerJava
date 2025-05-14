package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {
    int registerClient(VirtualClient client) throws RemoteException;
    void setNickname(int idClient, String nickname) throws RemoteException, IncorrectNameException, IncorrectClientException,
            SetGameModeException;

    void setGameInfo(GameInfo gameInfo) throws RemoteException;
}
