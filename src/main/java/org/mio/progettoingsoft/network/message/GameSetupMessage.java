package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.model.enums.GameMode;

public final class GameSetupMessage extends Message {
    private final int numPlayers;
    private final GameMode mode;

    public GameSetupMessage(Integer idGame, String nickname, Integer numPlayers, GameMode gameMode) {
        super(idGame, nickname);
        this.numPlayers = numPlayers;
        this.mode = gameMode;

    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public GameMode getMode() {
        return mode;
    }
}