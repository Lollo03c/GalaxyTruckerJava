package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {

    int registerClient(VirtualClient client) throws RemoteException;

    void handleNickname(int idClient, String nickname) throws RemoteException;
    void handleGameInfo(GameInfo gameInfo) throws RemoteException;
    void getCoveredComponent(int idGame, String nickname) throws RemoteException;
    void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException;
    void discardComponent(int idGame, int idComponent) throws RemoteException;
}
