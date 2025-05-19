package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSocket extends Client{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    @Override
    public void connect(){
        try{
            socket = new Socket("localhost", 1050);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            ClientMessageReceiver clientMessageReceiver = new ClientMessageReceiver(in, receivedMessages);
            new Thread(clientMessageReceiver).start();

            new Thread(this::handleMessage).start();


            sendMessage(new WelcomeMessage(-1, "", -1));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Message message){
        try{
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMessage(){
        while (true){
            try {
                Message message = receivedMessages.take();

                switch (message){
                    case WelcomeMessage welcomeMessage -> {
                        idClient = welcomeMessage.getClientId();
                    }
                    case StateMessage stateMessage -> {
                        controller.setState(stateMessage.getState());
                    }

                    case NicknameMessage nicknameMessage -> {
                        controller.setNickname(nicknameMessage.getNickname());
                    }

                    case GameIdMessage gameIdMessage -> {
                        controller.setGameId(gameIdMessage.getGameId());
                    }

                    case FlyBoardMessage flyBoardMessage -> {
                        controller.setFlyBoard(flyBoardMessage.getMode(), flyBoardMessage.getPlayers());
                    }

                    case ComponentMessage componentMessage -> {
                        switch (componentMessage.getAction()){
                            case COVERED -> controller.setInHandComponent(componentMessage.getIdComp());
                            case REMOVE -> {}
                            case ADD -> controller.addOtherComponent(componentMessage.getNickname(), componentMessage.getIdComp(),
                                    componentMessage.getCordinate(), componentMessage.getRotations());


                            case ADD_UNCOVERED ->
                                controller.addUncoveredComponent(componentMessage.getIdComp());
                        }
                    }

                    case DeckMessage deckMessage -> {
                        switch (deckMessage.getAction()){
                            case BOOK -> {
                                controller.setInHandDeck(deckMessage.getDeckNumber());
                            }

                            case REMOVE_FROM_CLIENT -> {
                                controller.removeDeck(deckMessage.getDeckNumber());
                            }

                            case UNBOOK ->
                                controller.addAvailableDeck(deckMessage.getDeckNumber());
                        }
                    }

                    default -> {}
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ;
        }
    }

    @Override
    public void ping(String msg){
        return ;
    }

    @Override
    public void handleNickname(String nickname){
        Message message = new NicknameMessage(-1, nickname, idClient);
        sendMessage(message);

    }

    @Override
    public void handleGameInfo(GameInfo gameInfo){
        Message message = new GameInfoMessage(-1, "", gameInfo);
        sendMessage(message);
    }

    @Override
    public void getCoveredComponent(int idGame){
        Message message = new ComponentMessage(idGame, controller.getNickname(), ComponentMessage.Action.COVERED, 0, null, 0);
        sendMessage(message);
    }

    @Override
    public void handleComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations){
        Message message = new ComponentMessage(idGame, nickname, ComponentMessage.Action.ADD, idComp, cordinate, rotations);
        sendMessage(message);
    }

    @Override
    public void discardComponent(int idComponent){
        Message message = new ComponentMessage(controller.getIdGame(), "", ComponentMessage.Action.DISCARD, idComponent, null, 0);
        sendMessage(message);
    }

    @Override
    public void drawUncovered(int idComponent){
        Message message = new ComponentMessage(controller.getIdGame(), controller.getNickname(), ComponentMessage.Action.DRAW_UNCOVERED, idComponent, null, 0);
        sendMessage(message);
    }

    @Override
    public void bookDeck(int deckNumber){
        Message message = new DeckMessage(controller.getIdGame(), controller.getNickname(), DeckMessage.Action.BOOK, deckNumber);
        sendMessage(message);
    }

    public void freeDeck(int deckNumber){
        Message message = new DeckMessage(controller.getIdGame(), controller.getNickname(), DeckMessage.Action.UNBOOK, deckNumber);
        sendMessage(message);
    }
}
