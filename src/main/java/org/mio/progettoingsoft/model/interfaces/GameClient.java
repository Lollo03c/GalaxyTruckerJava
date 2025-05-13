package org.mio.progettoingsoft.model.interfaces;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.model.enums.GameMode;

public interface GameClient {

    int getIdGame();
    void setupGame(GameMode mode, int nPlayers);
    GameMode getGameMode();
    int getNumPlayers();




}
