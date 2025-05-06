package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.VirtualClient;

public final class GameSetupInput extends Message {
    private final int numPlayers;
    private final GameMode mode;

    public GameSetupInput(int idGame, String nickname, int numPlayers, GameMode gameMode) {
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