package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.advCards.sealed.CardState;

public final class CardStateMessage extends Message{

    private final CardState state;

    public CardStateMessage(int gameId, String nickname, CardState state) {
        super(gameId, nickname);
        this.state = state;
    }

    public CardState getState() {
        return state;
    }
}
