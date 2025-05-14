package org.mio.progettoingsoft.network.server.messages;

public final class WelcomeMessage extends Message{
    private final int idClient;

    public WelcomeMessage(int idGame, String nickname, int idClient) {
        super(idGame, nickname);
        this.idClient = idClient;
    }

    public int getIdClient() {
        return idClient;
    }
}