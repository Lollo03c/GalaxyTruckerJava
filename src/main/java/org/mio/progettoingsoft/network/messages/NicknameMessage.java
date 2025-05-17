package org.mio.progettoingsoft.network.messages;

public final class NicknameMessage extends Message{
    private final int idClient;

    public NicknameMessage(int gameId, String nickname, int idClient) {
        super(gameId, nickname);
        this.idClient = idClient;
    }

    public int getIdClient() {
        return idClient;
    }
}
