package org.mio.progettoingsoft.network.messages;

public final class GameIdMessage extends Message {
    public GameIdMessage(int gameId, String nickname) {
        super(gameId, nickname);
    }
}
