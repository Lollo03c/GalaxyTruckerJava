package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.util.Map;

public final class FlyBoardMessage extends Message{
    GameMode mode;
    Map<String, HousingColor> players;

    public FlyBoardMessage(int gameId, String nickname, GameMode mode, Map<String, HousingColor> players) {
        super(gameId, nickname);
        this.mode = mode;
        this.players = players;
    }

    public GameMode getMode() {
        return mode;
    }

    public Map<String, HousingColor> getPlayers() {
        return players;
    }
}
