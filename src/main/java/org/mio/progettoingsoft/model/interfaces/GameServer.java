package org.mio.progettoingsoft.model.interfaces;

import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.network.server.GameController;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Interface that defines the server-side access point to a game instance.
 * <p>
 * It exposes only the methods required by the server to manage players,
 * configure the game, and interact with the game logic and event system.
 * <p>
 * This interface is distinct from the model exposed to the client,
 * and provides server-specific control over the game's internal state.
 */
public interface GameServer {
    /**
     * Starts the game.
     */
    void startGame();

    /**
     * Adds a player to the game with the given nickname and client reference.
     *
     * @param nickname the player's nickname
     * @param client the client's virtual interface
     */
    void addPlayer(String nickname, VirtualClient client);

    /**
     * Sets up the game mode and number of players.
     *
     * @param mode the selected game mode
     * @param numPlayers the expected number of players
     */
    void setupGame(GameMode mode, int numPlayers);

    /**
     * Returns the unique ID of the game instance.
     *
     * @return the game ID
     */
    int getIdGame();

    /**
     * Returns the total number of players in the game.
     *
     * @return number of players
     */
    int getNumPlayers();

    /**
     * Returns the configured game mode.
     *
     * @return the game mode
     */
    GameMode getGameMode();

    /**
     * Returns a map of nicknames and their associated VirtualClient interfaces.
     *
     * @return map of connected clients
     */
    Map<String, VirtualClient> getClients();

    /**
     * Returns true if the game has reached the maximum number of players.
     *
     * @return whether the game is full
     */
    boolean isFull();

    /**
     * Indicates whether the server should prompt for game settings.
     *
     * @return true if settings are required
     */
    boolean askSetting();

    /**
     * Returns the FlyBoard associated with the current game.
     *
     * @return the game's FlyBoard
     */
    FlyBoard getFlyboard();

    /**
     * Returns the GameController that manages the game logic.
     *
     * @return the game's controller
     */
    GameController getController();

    /**
     * Creates the FlyBoard with the given mode and player nicknames.
     *
     * @param mode the game mode
     * @param nicknames the set of player nicknames
     */
    void createFlyboard(GameMode mode, Set<String> nicknames);

    /**
     * Adds an event to the event queue.
     *
     * @param event the event to add
     */
    void addEvent(Event event);

    /**
     * Returns the blocking queue that stores game events.
     *
     * @return the events queue
     */
    BlockingQueue<Event> getEventsQueue();

    /**
     * Returns the lock object used to synchronize on the game instance.
     *
     * @return the synchronization lock
     */
    Object getLock();

    /**
     * Indicates whether the game is in test mode.
     *
     * @return true if testing is enabled
     */
    boolean isTesting();
}