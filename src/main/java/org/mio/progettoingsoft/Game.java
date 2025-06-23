package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.interfaces.GameClient;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Game implements GameServer, GameClient {
    private FlyBoard flyboard;
    private int idGame;
    private GameMode mode;
    private int numPlayers;

    private final BlockingQueue<Event> eventsQueue = new LinkedBlockingQueue<>();
    private final GameController gameController;
    private final Object lock = new Object();

    private final boolean testing;


    private Map<String, VirtualClient> clients = new HashMap<>();


    public Game(int idGame) {
        this.idGame = idGame;
        gameController = new GameController(this, eventsQueue);
        testing = false;
    }

    public Game(int idGame, boolean testing){
        this.idGame = idGame;
        gameController = new GameController(this, eventsQueue, testing);
        this.testing = testing;
    }

    @Override
    public void setupGame(GameMode mode, int numPlayers){

        synchronized (GameManager.getInstance().getLockCreatingGame()) {
            GameManager.getInstance().getCreatingGame().set(false);
            GameManager.getInstance().getLockCreatingGame().notifyAll();


            this.mode = mode;
            this.numPlayers = numPlayers;
        }
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
        return clients.isEmpty();
    }

    /**
     * once the game is full of the players starts the game on a different Thread
     */
    @Override
    public void startGame(){

        if (!testing)
            createFlyboard(mode, clients.keySet());

        gameController.registerListener();

        Map<String, HousingColor> colorMap = flyboard.getPlayers().stream()
                .collect(Collectors.toMap(
                        Player::getNickname,
                        Player::getColor
                ));
        List<List<Integer>> decks = flyboard.getLittleDecks();

        for (VirtualClient client : clients.values()){
            try {
                client.setFlyBoard(mode ,colorMap, decks);
                client.setState(GameState.GAME_START);
                client.setState(GameState.BUILDING_SHIP);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Logger.info("Game " + idGame + " started");
    }

//    @Override
//    public void addReceivedMessage(Message message){
//        receivedMessages.add(message);
//    }
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

    @Override
    public GameController getController(){
        return gameController;
    }

    public void createFlyboard(GameMode mode, Set<String> nicknames) {
        flyboard = FlyBoard.createFlyBoard(mode, nicknames);
    }

    @Override
    public void addEvent(Event event){
        eventsQueue.add(event);
    }

    @Override
    public BlockingQueue<Event> getEventsQueue(){
        return eventsQueue;
    }

    @Override
    public Object getLock(){
        return lock;
    }
}
