package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.interfaces.GameClient;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Represents a full game session, managing both server-side and client-side responsibilities.
 * <p>
 * This class implements both {@link GameServer} and {@link GameClient}, acting as the core
 * container for the game state, players, event handling, and controller coordination.
 * <p>
 * On the server side, it handles player management, game setup, and lifecycle control.
 * On the client side, it provides read-only access to game metadata such as mode and number of players.
 * <p>
 */
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


    /**
            * Constructs a new Game with the specified game ID.
            *
            * @param idGame the unique identifier of the game
     */
    public Game(int idGame) {
        this.idGame = idGame;
        gameController = new GameController(this, eventsQueue);
        testing = false;
    }

    /**
     * Constructs a new Game with the specified game ID and testing mode.
     *
     * @param idGame the unique identifier of the game
     * @param testing true if the game is in testing mode; false otherwise
     */
    public Game(int idGame, boolean testing){
        this.idGame = idGame;
        gameController = new GameController(this, eventsQueue, testing);
        this.testing = testing;
    }

    /**
     * Initializes the game with the specified mode and number of players.
     * <p>
     * This method also resets the creation flag in the GameManager singleton
     * and notifies all threads waiting for the game to be created.
     *
     * @param mode the selected game mode
     * @param numPlayers the expected number of players
     */
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

    /**
     * Adds a player to the game.
     *
     * @param nickname the player's nickname
     * @param client the VirtualClient representing the player
     */
    @Override
    public void addPlayer(String nickname, VirtualClient client){
        clients.put(nickname, client);
    }

    /**
     * Determines if game settings should be requested (usually at initialization).
     *
     * @return true if no players have joined yet; false otherwise
     */
    @Override
    public boolean isFull(){
        return numPlayers == clients.size();
    }

    /**
     * Determines if game settings should be requested (usually at initialization).
     *
     * @return true if no players have joined yet; false otherwise
     */
    @Override
    public boolean askSetting() {
        return clients.isEmpty();
    }

    /**
     * Starts the game in a new thread once all players have joined.
     * <p>
     * Initializes the FlyBoard, registers the event listeners,
     * distributes initial game data to clients, and starts the hourglass timer.
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

//        flyboard.startHourglass(idGame);
//        for(VirtualClient client : getClients().values()){
//            try {
//                client.startedHourglass(idGame);
//            }catch (Exception e){
//                throw new RuntimeException(e);
//            }
//        }
        Logger.info("Game " + idGame + " started");
    }

    @Override
    public FlyBoard getFlyboard(){
        return flyboard;
    }

    @Override
    public GameController getController(){
        return gameController;
    }

    /**
     * Creates and initializes the FlyBoard for the current game session.
     *
     * @param mode the selected game mode
     * @param nicknames the set of player nicknames to include
     */
    public void createFlyboard(GameMode mode, Set<String> nicknames) {
        flyboard = FlyBoard.createFlyBoard(mode, nicknames);
    }

    /**
     * add en event to precess
     * @param event the event to add
     */
    @Override
    public void addEvent(Event event){
        eventsQueue.add(event);
    }

    /**
     *
     * @return the {@link BlockingQueue} of {@link Event} to process
     */
    @Override
    public BlockingQueue<Event> getEventsQueue(){
        return eventsQueue;
    }

    @Override
    public Object getLock(){
        return lock;
    }

    @Override
    public boolean isTesting() {
        return testing;
    }
}
