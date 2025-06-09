package org.mio.progettoingsoft.network.messages;

public final class DoubleEngineMessage extends Message{
    private final int number;

    public DoubleEngineMessage(int gameId, String nickname, int number) {
        super(gameId, nickname);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
