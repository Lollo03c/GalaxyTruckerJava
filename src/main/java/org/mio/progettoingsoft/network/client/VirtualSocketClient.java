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
import java.util.List;
import java.util.Map;

/*NOT USED CLASS*/

/*
public class VirtualSocketClient implements VirtualClient {
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

    /**
     * Metodi derivati da VirtualClient, ovvero i metodi che vengono chiamati dal server


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
    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players, List<List<Integer>> decks) throws RemoteException {
        Message message = new FlyBoardMessage(-1, "", mode, players, decks);
        sendMessage(message);
    }

    @Override
    public void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws RemoteException {
        Message message = new ComponentMessage(0, nickname, ComponentMessage.Action.ADD, idComp, cordinate, rotations );
        sendMessage(message);
    }

    @Override
    public void setInHandComponent(int idComp){
        Message message = new ComponentMessage(0, "", ComponentMessage.Action.COVERED, idComp, null, 0);
        sendMessage(message);
    }

    @Override
    public void addUncoveredComponent(int idComp){
        Message message = new ComponentMessage(0, "", ComponentMessage.Action.ADD_UNCOVERED, idComp, null, 0);
        sendMessage(message);
    }

    @Override
    public void removeUncovered(int idComp){
        Message message = new ComponentMessage(0, "", ComponentMessage.Action.DRAW_UNCOVERED, idComp, null, 0);
        sendMessage(message);
    }

    @Override
    public void removeDeck(Integer idDeck){
        Message message = new DeckMessage(0, "", DeckMessage.Action.REMOVE_FROM_CLIENT, idDeck);
        sendMessage(message);
    }

    @Override
    public void setInHandDeck(int deckNumber){
        Message message = new DeckMessage(0, "", DeckMessage.Action.BOOK, deckNumber);
        sendMessage(message);
    }

    @Override
    public void addAvailableDeck(int deckNumber){
        Message message = new DeckMessage(0, "", DeckMessage.Action.UNBOOK, deckNumber);
        sendMessage(message);
    }
}

*/