package org.mio.progettoingsoft.network.SerMessage;

public final class GameSetupInput2 extends SerMessage{
    int numPlayers;
    public GameSetupInput2(String nickname, int numPlayers) {
        super(nickname);
        this.numPlayers = numPlayers;
    }
    public int getNumPlayers() {
        return numPlayers;
    }
}
