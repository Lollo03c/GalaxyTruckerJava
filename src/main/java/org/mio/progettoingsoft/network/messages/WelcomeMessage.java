package org.mio.progettoingsoft.network.messages;

public final class WelcomeMessage extends Message {
    private final int clientId;

    public WelcomeMessage(int gameId, String nickname, int clientId) {
        super(gameId, nickname);
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }
}
