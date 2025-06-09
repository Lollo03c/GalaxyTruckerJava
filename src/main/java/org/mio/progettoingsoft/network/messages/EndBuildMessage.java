package org.mio.progettoingsoft.network.messages;

public final class EndBuildMessage extends Message{

    public EndBuildMessage(int gameId, String nickname) {
        super(gameId, nickname);
    }
}
