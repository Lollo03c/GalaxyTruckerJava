package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.model.enums.GameInfo;

public final class GameInfoMessage extends Message{
    private final GameInfo gameInfo;

    public GameInfoMessage(int gameId, String nickname, GameInfo gameInfo) {
        super(gameId, nickname);
        this.gameInfo = gameInfo;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
