package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameClient;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.input.Input;
import org.mio.progettoingsoft.network.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Game implements GameServer, GameClient {
    private FlyBoard flyboard;
    private final int idGame;
    private GameMode mode;
    private int numPlayers;

    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();
    private final GameController gameController = new GameController();

    private Map<String, VirtualClient> clients = new HashMap<>();

    private GameState gameState;

    public Game(int idGame) {
        this.idGame = idGame;
    }

    @Override
    public void setupGame(GameMode mode, int numPlayers){
        this.mode = mode;
        this.numPlayers = numPlayers;
    }

    @Override
    public int getIdGame(){
        return idGame;
    }

    @Override
    public int getNumPlayers(){
        return numPlayers;
    }

    @Override
    public GameMode getGameMode(){
        return  mode;
    }

    @Override
    public Map<String, VirtualClient> getClients(){
        return clients;
    }

    @Override
    public void addPlayer(String nickname, VirtualClient client){
        clients.put(nickname, client);
    }

    public boolean isFull(){
        return numPlayers == clients.size();
    }

    @Override
    public boolean askSetting() {
        return clients.size() == 1;
    }

    /**
     * once the game is full of the players starts the game on a different Thread
     */
    @Override
    public void startGame(){
        flyboard = FlyBoard.createFlyBoard(mode, clients.keySet());
        gameController.setGame(this);

        //game modifica il suo stato
        setGameState(GameState.BUILDING_SHIP);
        gameController.update(gameState);

        gameController.setReceivedMessages(receivedMessages);
        gameController.run();

    }

    @Override
    public void addReceivedMessage(Message message){
        receivedMessages.add(message);
    }

    public void setGameState(GameState newState){
        this.gameState = newState;
    }

    @Override
    public int getCoveredComponent() throws IncorrectFlyBoardException {
        Integer idComp;
        try{
            idComp = flyboard.getCoveredComponents().removeLast();
            return idComp;
        }
        catch (NoSuchElementException e){
            throw new IncorrectShipBoardException("");
        }

    }

    @Override
    public VirtualClient getClient(String nickname){
        return clients.get(nickname);
    }

    @Override
    public FlyBoard getFlyboard(){
        return flyboard;
    }
//
//    private void handleMessagesFromServer(){
//        while (true){
//            Message message = receivedMessages.poll();
//
//            gameController.handleMessage(message);
//
//        }
//    }



}
