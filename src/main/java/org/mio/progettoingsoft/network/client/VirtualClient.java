package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface VirtualClient extends Remote {

    void ping(String msg) throws RemoteException;
    void setNickname(String nickname) throws RemoteException;
    void askGameSettings(String nickname) throws RemoteException;
    void wrongNickname() throws RemoteException;
    void setGameId(int gameId) throws RemoteException;

    void setState(GameState state) throws RemoteException;

    void setFlyBoard(GameMode mode, Map<String, HousingColor> players) throws RemoteException;
    void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException;

}
