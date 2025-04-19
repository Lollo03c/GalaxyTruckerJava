package org.mio.progettoingsoft.network.message;

public final class JoinedGameMessage extends Message {
    public JoinedGameMessage(String nickname) {
        super(nickname);
    }
}