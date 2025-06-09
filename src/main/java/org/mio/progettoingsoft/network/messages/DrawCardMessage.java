package org.mio.progettoingsoft.network.messages;

public final class DrawCardMessage extends Message{
    private final int idCard;

    public DrawCardMessage(int gameId, String nickname, int idCard) {
        super(gameId, nickname);
        this.idCard = idCard;
    }

    public int getIdCard() {
        return idCard;
    }
}
