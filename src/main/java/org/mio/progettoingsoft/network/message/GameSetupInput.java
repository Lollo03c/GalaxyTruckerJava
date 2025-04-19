package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.network.VirtualClient;

public final class GameSetupInput extends Message {
    int numPlayers;

    public GameSetupInput(String nickname, int numPlayers) {
        super(nickname);
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}