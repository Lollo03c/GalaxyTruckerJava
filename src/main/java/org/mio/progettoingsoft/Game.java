package org.mio.progettoingsoft;

import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.StartGameMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Game {
    private final FlyBoard flyboard;
    private final int idGame;
    private GameMode mode;
    private int numPlayers;

    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();
    private final GameController gameController = new GameController();

    private Map<String, VirtualClient> clients = new HashMap<>();

    public Game(int idGame) {
        this.idGame = idGame;

        this.flyboard = new FlyBoard();
    }

    public void setupGame(GameMode mode, int numPlayers){
        this.mode = mode;
        this.numPlayers = numPlayers;
    }

    public int getIdGame(){
        return idGame;
    }

    public int getNumPlayers(){
        return numPlayers;
    }

    public GameMode getGameMode(){
        return  mode;
    }

    public Map<String, VirtualClient> getClients(){
        return clients;
    }

    public void addPlayer(String nickname, VirtualClient client){
        clients.put(nickname, client);
    }

    public boolean isFull(){
        return numPlayers == clients.size();
    }

    public boolean askSetting() {
        return clients.size() == 1;
    }

    public void startGame() throws Exception {
        broadcast(new StartGameMessage(idGame));
    }

    public void addReceivedMessage(Message message){
        receivedMessages.add(message);
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

    private void broadcast(Message message) throws Exception {
        for (VirtualClient client : clients.values())
            client.showUpdate(message);
    }


}
