package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.model.enums.GameMode;

public final class WaitingForPlayerMessage extends Message{

    private final int nPlayers;
    private final GameMode mode;

    public WaitingForPlayerMessage(int gameId, String nickname, int nPlayers, GameMode mode){
        super(gameId, nickname);

        this.nPlayers = nPlayers;
        this.mode = mode;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public GameMode getMode() {
        return mode;
    }
}
