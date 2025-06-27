package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.model.components.GoodType;

public final class GoodMessage extends Message{
    public enum GoodMessageType{
        ADD_GOOD, REMOVE_GOOD,
        REMOVE_PENDING, ADD_PENDING
    };

    private final GoodMessageType typeMessage;
    private final int idComp;
    private final GoodType goodType;

    public GoodMessage(int gameId, String nickname, GoodMessageType typeMessage, int idComp, GoodType goodType) {
        super(gameId, nickname);
        this.typeMessage = typeMessage;
        this.idComp = idComp;
        this.goodType = goodType;
    }

    public GoodType getGoodType() {
        return goodType;
    }

    public int getIdComp() {
        return idComp;
    }

    public GoodMessageType getTypeMessage() {
        return typeMessage;
    }
}
