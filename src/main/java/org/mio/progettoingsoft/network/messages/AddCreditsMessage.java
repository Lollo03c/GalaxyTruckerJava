package org.mio.progettoingsoft.network.messages;

public final class AddCreditsMessage extends Message{

    private final int credits;

    public AddCreditsMessage(int gameId, String nickname, int credits) {
        super(gameId, nickname);
        this.credits = credits;
    }

    public int getCredits() {
        return credits;
    }
}
