package org.mio.progettoingsoft.network.message;

public final class NicknameMessage extends Message {
    private final int idPlayer;

    public NicknameMessage(String nickname, int idPlayer) {
        super(null, nickname);

        this.idPlayer = idPlayer;
    }

    public int getIdPlayer() {
        return idPlayer;
    }
}