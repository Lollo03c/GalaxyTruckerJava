package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.HousingColor;

public class Game {
    private FlyBoard flyboard;
    private int numPlayers;


    public Game(int numPlayers, String host) {
        this.numPlayers = numPlayers;
        this.flyboard = new FlyBoard();
        flyboard.addPlayer(host, HousingColor.BLUE);
    }

    public FlyBoard getFlyboard() {
        return flyboard;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void addPlayer(String nickname, HousingColor color) {
        this.flyboard.addPlayer(nickname, color);
    }

    public int leftPlayers() {
        return (this.numPlayers - flyboard.getScoreBoard().size() + 1);
    }
}
