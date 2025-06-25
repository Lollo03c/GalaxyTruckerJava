package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.components.GuestType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class AddCrewMessage extends Message{
    private final Map<Cordinate, List<GuestType>> crewAdded;
    private final Cordinate cord;
    private final GuestType type;

    public AddCrewMessage(int gameId, String nickname, Map<Cordinate, List<GuestType>> crewAdded) {
        super(gameId, nickname);
        this.crewAdded = crewAdded;
        cord = null;
        type = null;
    }

    public AddCrewMessage(int gameId, String nickname, Cordinate cord, GuestType type) {
        super(gameId, nickname);
        this.cord = cord;
        this.type = type;
        crewAdded = null;
    }

    public Map<Cordinate, List<GuestType>> getCrewAdded() {
        return crewAdded;
    }

    public Cordinate getCord() {
        return cord;
    }

    public GuestType getType() {
        return type;
    }
}
