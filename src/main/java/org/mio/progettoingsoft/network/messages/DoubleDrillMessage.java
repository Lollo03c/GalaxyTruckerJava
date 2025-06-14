package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.Cordinate;

import java.util.List;

public final class DoubleDrillMessage extends Message{
    private final List<Cordinate> drillCordinates;

    public DoubleDrillMessage(int gameId, String nickname, List<Cordinate> drillCordinates) {
        super(gameId, nickname);
        this.drillCordinates = drillCordinates;
    }

    public List<Cordinate> getDrillCordinates() {
        return drillCordinates;
    }
}
