package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.model.Cordinate;

import java.util.List;

public final class CrewRemoveMessage extends Message{

    private final List<Cordinate> cordinates;
    private final int idComp;

    public CrewRemoveMessage(int gameId, String nickname, List<Cordinate> housingCordinates) {
        super(gameId, nickname);
        cordinates = housingCordinates;
        idComp = -1;
    }

    public CrewRemoveMessage(int gameId, String nickname, int idComp) {
        super(gameId, nickname);
        this.cordinates = null;
        this.idComp = idComp;
    }

    public List<Cordinate> getCordinates() {
        return cordinates;
    }

    public int getIdComp() {
        return idComp;
    }
}
