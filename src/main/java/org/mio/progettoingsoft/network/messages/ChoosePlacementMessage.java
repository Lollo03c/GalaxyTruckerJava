package org.mio.progettoingsoft.network.messages;

import java.util.List;

public final class ChoosePlacementMessage extends Message{

    private final int place;

    public ChoosePlacementMessage(int gameId, String nickname, int place  ) {
        super(gameId, nickname);
        this.place = place;
    }

    public int getPlace() {
        return place;
    }
}
