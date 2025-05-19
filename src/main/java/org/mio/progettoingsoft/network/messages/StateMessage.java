package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.GameState;

public final class StateMessage extends Message{
    private final GameState state;

    public StateMessage(int gameId, String nickname, GameState state) {
        super(gameId, nickname);
        this.state = state;
    }

    public GameState getState() {
        return state;
    }
}
