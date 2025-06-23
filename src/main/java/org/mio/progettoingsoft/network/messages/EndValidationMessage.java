package org.mio.progettoingsoft.network.messages;

public final class EndValidationMessage extends Message{

    public EndValidationMessage(int gameId, String nickname) {
        super(gameId, nickname);
    }
}
