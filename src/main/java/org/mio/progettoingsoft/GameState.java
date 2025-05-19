package org.mio.progettoingsoft;

import java.io.Serializable;

public enum GameState implements Serializable {
    WAITING, START, NICKNAME, GAME_MODE, WAITING_PLAYERS,

    GAME_START, BUILDING_SHIP, COMPONENT_MENU, ADD_COMPONENT, VIEW_SHIP_BUILDING,

    ERROR_NICKNAME, ERROR_PLACEMENT;
}
