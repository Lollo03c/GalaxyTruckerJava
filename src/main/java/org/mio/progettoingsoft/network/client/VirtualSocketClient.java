package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.Map;

public class VirtualSocketClient implements VirtualClient{
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public VirtualSocketClient(ObjectInputStream in, ObjectOutputStream out){
        this.outputStream = out;
        this.inputStream = in;
    }

    private void sendMessage(Message message){
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            outputStream.reset();
        }
        catch (IOException e){

        }
    }

    @Override
    public void ping(String msg) throws RemoteException {

    }

    @Override
    public void setNickname(String nickname) throws RemoteException {
        Message message = new NicknameMessage(-1, nickname, -1);
        sendMessage(message);
    }

    @Override
    public void askGameSettings(String nickname) throws RemoteException {
        Message message = new StateMessage(0, "", GameState.GAME_MODE);
        sendMessage(message);
    }

    @Override
    public void wrongNickname() throws RemoteException {
        Message message = new StateMessage(0, "", GameState.ERROR_NICKNAME);
        sendMessage(message);
    }

    @Override
    public void setGameId(int gameId) throws RemoteException {
        Message message = new GameIdMessage(gameId, "");
        sendMessage(message);
    }

    @Override
    public void setState(GameState state) throws RemoteException {
        Message message = new StateMessage(0, "", state);
        sendMessage(message);
    }

    @Override
    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players) throws RemoteException {
        Message message = new FlyBoardMessage(-1, "", mode, players);
        sendMessage(message);
    }

    @Override
    public void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException {
        Message message = new ComponentMessage(0, nickname, ComponentMessage.Action.ADD, idComp, cordinate, rotations );
        sendMessage(message);
    }
}
