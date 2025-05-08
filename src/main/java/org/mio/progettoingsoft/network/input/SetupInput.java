package org.mio.progettoingsoft.network.input;

import org.mio.progettoingsoft.model.enums.GameMode;

public final class SetupInput extends Input {
    private final int nPlayers;
    private final GameMode gameMode;

    public SetupInput(int nPlayers, GameMode gameMode) {
        this.nPlayers = nPlayers;
        this.gameMode = gameMode;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}
