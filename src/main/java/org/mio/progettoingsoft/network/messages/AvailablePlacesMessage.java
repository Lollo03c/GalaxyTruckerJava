package org.mio.progettoingsoft.network.messages;

import java.util.List;

public final class AvailablePlacesMessage extends Message{

    private final List<Integer> availablePlaces;
    public AvailablePlacesMessage(int gameId, String nickname, List<Integer> availablePlaces ) {
        super(gameId, nickname);
        this.availablePlaces = availablePlaces;
    }

    public List<Integer> getAvailablePlaces() {
        return availablePlaces;
    }
}
