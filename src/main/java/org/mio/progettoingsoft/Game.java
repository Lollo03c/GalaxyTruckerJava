package org.mio.progettoingsoft;

import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameClient;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Game implements GameServer, GameClient {
    private FlyBoard flyboard;
    private final int idGame;
    private GameMode mode;
    private int numPlayers;

    private final BlockingQueue<Object> receivedMessages = new LinkedBlockingQueue<>();
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

        for (VirtualClient client : clients.values()) {
            try {
                client.setState(GameState.GAME_START);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

    }

//    @Override
//    public void addReceivedMessage(Message message){
//        receivedMessages.add(message);
//    }

    @Override
    public FlyBoard getFlyBoard(){
        return this.flyboard;
    }

    public void setGameState(GameState newState){
        this.gameState = newState;
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
