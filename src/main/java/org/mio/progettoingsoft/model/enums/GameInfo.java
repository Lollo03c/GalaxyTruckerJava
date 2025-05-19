package org.mio.progettoingsoft.model.enums;

import java.io.Serializable;

/**
* record representing all the information about a game
 *
 * @Param gameId : int
 * @Param mode : {@link GameMode}
 * @Param nPlayer : int representing number of players;
*/
public record GameInfo(int gameId, GameMode mode, int nPlayers) implements Serializable {
}
