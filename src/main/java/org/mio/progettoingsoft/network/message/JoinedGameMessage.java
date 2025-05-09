package org.mio.progettoingsoft.network.message;

public final class JoinedGameMessage extends Message {
    public JoinedGameMessage(int idGame, String nickname) {
        super(idGame, nickname);
    }
}