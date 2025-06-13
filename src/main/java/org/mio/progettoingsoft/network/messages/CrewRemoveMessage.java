package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.Cordinate;

import java.util.List;

public final class CrewRemoveMessage extends Message{

    private final List<Cordinate> cordinates;
    public CrewRemoveMessage(int gameId, String nickname, List<Cordinate> housingCordinates) {
        super(gameId, nickname);
        cordinates = housingCordinates;
    }

    public List<Cordinate> getCordinates() {
        return cordinates;
    }




}
