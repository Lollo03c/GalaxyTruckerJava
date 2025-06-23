package org.mio.progettoingsoft.network.messages;

public final class AdvancePlayerMessage extends Message{
    private final int steps;


    public AdvancePlayerMessage(int gameId, String nickname, int steps) {
        super(gameId, nickname);
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
}
