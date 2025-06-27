package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.model.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.util.List;
import java.util.Map;

public final class FlyBoardMessage extends Message{
    GameMode mode;
    Map<String, HousingColor> players;
    List<List<Integer>> decks;

    public FlyBoardMessage(int gameId, String nickname, GameMode mode, Map<String, HousingColor> players, List<List<Integer>> decks) {
        super(gameId, nickname);
        this.mode = mode;
        this.players = players;
        this.decks = decks;
    }

    public GameMode getMode() {
        return mode;
    }

    public Map<String, HousingColor> getPlayers() {
        return players;
    }

    public List<List<Integer>> getDecks() {
        return decks;
    }
}
