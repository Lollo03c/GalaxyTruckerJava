package org.mio.progettoingsoft.network.messages;

public final class RollDiceMessage extends Message{
    private final int first;
    private final int second;

    public RollDiceMessage(int gameId, String nickname, int first, int second) {
        super(gameId, nickname);
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }
}
