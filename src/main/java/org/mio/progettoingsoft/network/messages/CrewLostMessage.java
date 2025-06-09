package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.advCards.sealed.CardState;

import java.util.List;

public final class CrewLostMessage extends Message{

    private final List<Cordinate> cordinates;
    public CrewLostMessage(int gameId, String nickname,  List<Cordinate> housingCordinates) {
        super(gameId, nickname);
        cordinates = housingCordinates;
    }

    public List<Cordinate> getCordinates() {
        return cordinates;
    }




}
