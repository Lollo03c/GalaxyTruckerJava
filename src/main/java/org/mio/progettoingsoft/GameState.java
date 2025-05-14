package org.mio.progettoingsoft;

import java.io.Serializable;

public enum GameState implements Serializable {
    WAITING, START, NICKNAME, GAME_MODE,

    GAME_START,

    ERROR_NICKNAME;
}
