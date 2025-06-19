package org.mio.progettoingsoft.network.messages;

public final class RollDiceMessage extends Message{
    private final int number;

    public RollDiceMessage(int gameId, String nickname, int number) {
        super(gameId, nickname);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
