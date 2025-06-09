package org.mio.progettoingsoft;

import java.io.Serializable;

public enum GameState implements Serializable {
    WAITING, START, NICKNAME, GAME_MODE, WAITING_PLAYERS,

    GAME_START, BUILDING_SHIP, COMPONENT_MENU, ADD_COMPONENT, DRAW_UNCOVERED_COMPONENTS,
    VIEW_SHIP_BUILDING, VIEW_DECKS_LIST, VIEW_DECK, SWITCH_BOOKED, END_BUILDING, CHOOSE_POSITION,

    ERROR_NICKNAME, ERROR_PLACEMENT, UNABLE_UNCOVERED_COMPONENT, UNABLE_DECK, WRONG_POSITION,

    ADVENTURE_STARTED, DRAW_CARD, CARD_EFFECT,

    STARDUST, SLAVERS , ABANDONEDSHIP, ABANDONEDSTATION, COMBATZONE, EPIDEMIC, METEORSWARM, OPENSPACE, PIRATES, PLANETS,
    SMUGGLERS,

    ENGINE_CHOICE

    ;

    public static GameState stringToGameState(String string){
        switch(string){
            case "STARDUST" : return STARDUST;
            case "SLAVERS" : return SLAVERS;
            case "ABANDONEDSTATION" : return ABANDONEDSTATION;
            case "ABANDONEDSHIP" : return ABANDONEDSHIP;
            case "COMBATZONE" : return COMBATZONE;
            case "EPIDEMIC" : return EPIDEMIC;
            case "METEORSWARM" : return METEORSWARM;
            case "PIRATES" : return PIRATES;
            case "PLANETS" : return PLANETS;
            case "SMUGGLERS" : return SMUGGLERS;
            default : return STARDUST;
        }
    }
}
