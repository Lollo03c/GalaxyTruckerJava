package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.model.Game;
import org.mio.progettoingsoft.model.enums.GameState;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages all ongoing game instances and the creation of new games.
 * This class is implemented as a Singleton to ensure only one instance
 * manages all game states and client connections on the server.
 * It tracks connected clients, handles player registration to games,
 * and orchestrates the lifecycle of game instances.
 */
public class GameManager{
    private static GameManager instance;

    private AtomicInteger nextIdPlayer = new AtomicInteger(0);
    private AtomicInteger nextIdGame = new AtomicInteger(0);

    private final List<String> nicknames = new ArrayList<>();

    private Map<Integer, GameServer> ongoingGames;
    private GameServer waitingGame = null;

    private final Map<Integer, VirtualClient> clientsToAccept = new ConcurrentHashMap<>();

    private final Object lockCreatingGame = new Object();
    private final AtomicBoolean creatingGame = new AtomicBoolean(false);

    /**
     * Returns the singleton instance of {@code GameManager}.
     * This method is thread-safe and ensures that only one instance of GameManager exists.
     *
     * @return The singleton instance of the class.
     */
    public static synchronized GameManager getInstance(){
        if (instance == null)
            instance = new GameManager();

        return instance;
    }

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the map for ongoing games.
     */
    public GameManager(){
        ongoingGames = new HashMap<>();
    }

    /**
     * Retrieves the current game that is in a waiting state for players or settings.
     * If no game is currently waiting, a new {@link Game} instance is created.
     *
     * @return The {@link GameServer} instance that is currently waiting.
     */
    public synchronized GameServer getWaitingGame(){
        if (waitingGame == null) {
            waitingGame = new Game(getNextGameIdToStart());
            Logger.debug("Created new waiting game.");
        }
        return waitingGame;
    }

    /**
     * Returns a {@link Map} representing all games that are currently in progress.
     *
     * @return A {@code Map<Integer, GameServer>} where keys are game IDs and values are {@link GameServer} instances.
     */
    public synchronized Map<Integer, GameServer> getOngoingGames() {
        return ongoingGames;
    }

    /**
     * Adds a new {@link VirtualClient} to the list of clients awaiting game assignment.
     * Assigns a unique ID to the client.
     *
     * @param client The {@link VirtualClient} to be added.
     * @return The unique integer ID assigned to the client.
     */
    public int addClientToAccept(VirtualClient client){
            while (clientsToAccept.containsKey(nextIdPlayer.getAndIncrement())){

            }
            int idClient = nextIdPlayer.getAndIncrement();
            clientsToAccept.put(idClient, client);

            Logger.debug("Client " + idClient + " connected to server.");

            return idClient;
    }

    /**
     * Resets the {@code waitingGame} to {@code null}, indicating that no game is
     * currently accepting new players or configurations. This is typically called
     * after a game has started or been cancelled.
     */
    public synchronized void emptyWaitingGame(){
        waitingGame = null;
    }

    /**
     * Adds a new nickname to the list of active nicknames.
     * This is used to track occupied nicknames across all games.
     *
     * @param nick The nickname {@link String} to add.
     */
    public synchronized void addNickname(String nick){
        nicknames.add(nick);
    }

    /**
     * Generates and returns the next available unique ID for a new game.
     * It ensures that the ID is not already in use by any ongoing game.
     *
     * @return The next available integer game ID.
     */
    private synchronized int getNextGameIdToStart(){
        while (ongoingGames.containsKey(nextIdGame.get())){
            nextIdGame.set(nextIdGame.get() + 1);
        }
        return nextIdGame.getAndIncrement();
    }

    /**
     * Attempts to add a connected client (identified by {@code idClient}) to a game
     * using the provided {@code nickname}.
     * Handles nickname uniqueness, game creation, and player assignment to the waiting game.
     *
     * @param idClient The unique ID of the client connecting.
     * @param nickname The desired nickname for the player.
     * @throws RuntimeException if there's an issue with client communication or game state.
     */
    public void addPlayerToGame(int idClient, String nickname) {
        if (!clientsToAccept.containsKey(idClient)) {
            return;
        }

        VirtualClient client = clientsToAccept.get(idClient);
        if (nicknames.contains(nickname)) {
            try {
                client.wrongNickname();
                return;
            } catch (Exception e) {
                clientsToAccept.remove(idClient);
            }
        }

        try {
            client.setNickname(nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        synchronized (lockCreatingGame) {
           while (creatingGame.get()) {
               try {
                   lockCreatingGame.wait();
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }

           GameServer gameToStart = getWaitingGame();
           if (gameToStart.askSetting()){
               try{
                   creatingGame.set(true);

                   client.setGameId(gameToStart.getIdGame());
                   client.askGameSettings(nickname);

                   gameToStart.addPlayer(nickname, client);
                   clientsToAccept.remove(idClient);
                   nicknames.add(nickname);

                   Logger.info("Added player " + nickname + " to game " + gameToStart.getIdGame());
               }
               catch (Exception e){
                   throw new RuntimeException(e);
               }
           }
           else {
               try {
                   client.setGameId(gameToStart.getIdGame());
                   client.setState(GameState.WAITING_PLAYERS);

                   gameToStart.addPlayer(nickname, client);

                   Logger.info("Added player " + nickname + " to game " + gameToStart.getIdGame());

                   clientsToAccept.remove(idClient);
                   nicknames.add(nickname);

               } catch (Exception e) {
                    throw new RuntimeException(e);
               }

               if (gameToStart.getClients().size() == gameToStart.getNumPlayers()){
                   GameServer ready = gameToStart;
                   ongoingGames.put(ready.getIdGame(), ready);

                   waitingGame = null;
                   new Thread(ready::startGame).start();
               }
           }
        }
    }

    /**
     * Returns the AtomicBoolean flag indicating if a game is currently being created/configured.
     * This is useful for external synchronization.
     *
     * @return The {@link AtomicBoolean} flag for {@code creatingGame}.
     */
    public AtomicBoolean getCreatingGame(){
        return this.creatingGame;
    }

    /**
     * Returns the lock object used for synchronizing game creation.
     * This allows other components to wait on this lock.
     *
     * @return The {@link Object} used as a lock.
     */
    public Object getLockCreatingGame(){
        return lockCreatingGame;
    }

    /**
     * Removes an ongoing game from the {@code ongoingGames} map.
     * This is typically called when a game concludes or crashes.
     *
     * @param idGame The ID of the game to remove.
     */
    public void removeOnGoingGame(int idGame){
        GameServer removedGame = ongoingGames.remove(idGame);
        if (removedGame != null) {
            Logger.info("Game " + idGame + " removed from ongoing games.");
            // Optionally, also remove nicknames associated with this game if they are no longer in use.
            // This would require iterating through players of the removed game.
        } else {
            Logger.warning("Attempted to remove game " + idGame + " but it was not found in ongoingGames.");
        }
    }
}
