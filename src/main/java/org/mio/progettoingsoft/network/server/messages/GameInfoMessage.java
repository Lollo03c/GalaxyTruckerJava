package org.mio.progettoingsoft.network.server.messages;

import org.mio.progettoingsoft.model.enums.GameInfo;

public final class GameInfoMessage extends Message{

    private final GameInfo gameInfo;

    public GameInfoMessage(int idGame, String nickname, GameInfo gameInfo) {
        super(idGame, nickname);
        this.gameInfo = gameInfo;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
