package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.HousingColor;

public class Game {
    private int numPlayers;
    private FlyBoard flyboard;

    public Game(int numPlayers, String host) {
        this.numPlayers = numPlayers;
        this.flyboard = new FlyBoard();
        flyboard.addPlayer(host, HousingColor.BLUE);
    }
}
