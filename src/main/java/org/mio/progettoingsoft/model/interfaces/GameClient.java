package org.mio.progettoingsoft.model.interfaces;

import org.mio.progettoingsoft.model.enums.GameMode;

/**
 * Interface that defines the local client-side view of a game instance.
 * <p>
 * It provides read-only access to basic game configuration data
 * such as the game ID, mode, and number of players.
 * <p>
 * This interface is used on the client to interact with the local
 * representation of the game, distinct from server-side logic.
 */
public interface GameClient {

    /**
     * Returns the unique ID of the game associated with the client.
     *
     * @return the game ID
     */
    int getIdGame();

    /**
     * Configures the local game instance with the specified mode and number of players.
     *
     * @param mode the selected game mode
     * @param nPlayers the number of players
     */
    void setupGame(GameMode mode, int nPlayers);

    /**
     * Returns the configured game mode.
     *
     * @return the game mode
     */
    GameMode getGameMode();

    /**
     * Returns the number of players expected in the game.
     *
     * @return number of players
     */
    int getNumPlayers();
}
