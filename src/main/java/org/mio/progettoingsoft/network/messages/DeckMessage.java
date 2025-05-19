package org.mio.progettoingsoft.network.messages;

public final class DeckMessage extends Message{
    public enum Action{
        BOOK, UNBOOK, REMOVE_FROM_CLIENT;
    }

    private final int deckNumber;
    private final Action action;

    public DeckMessage(int gameId, String nickname, Action action, int deckNumber) {
        super(gameId, nickname);
        this.deckNumber = deckNumber;
        this.action = action;
    }

    public int getDeckNumber() {
        return deckNumber;
    }

    public Action getAction() {
        return action;
    }
}
