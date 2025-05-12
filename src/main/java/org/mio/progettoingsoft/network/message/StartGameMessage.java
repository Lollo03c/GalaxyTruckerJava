package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.components.HousingColor;

import java.util.Map;
import java.util.Set;

public final class StartGameMessage extends Message{

    private final Set<String> players;
    public StartGameMessage(int idGame, Set<String> players){
        super(idGame, Message.getBroadcastAddress());
        this.players = players;
    }

    public Set<String> getPlayers() {
        return players;
    }
}
