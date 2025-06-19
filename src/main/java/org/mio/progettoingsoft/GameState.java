package org.mio.progettoingsoft;

import java.io.Serializable;

public enum GameState implements Serializable {
    WAITING, START, NICKNAME, GAME_MODE, WAITING_PLAYERS, IDLE,

    GAME_START, BUILDING_SHIP, COMPONENT_MENU, ADD_COMPONENT, DRAW_UNCOVERED_COMPONENTS,
    VIEW_SHIP_BUILDING, VIEW_DECKS_LIST, VIEW_DECK, SWITCH_BOOKED, END_BUILDING, CHOOSE_POSITION, VIEW_BOOKED, CHOICE_BUILT,

    ERROR_NICKNAME, ERROR_PLACEMENT, UNABLE_UNCOVERED_COMPONENT, UNABLE_DECK, WRONG_POSITION, INVALID_SHIP_CHOICE,

    ADVENTURE_STARTED, DRAW_CARD, CARD_EFFECT, YOU_CAN_DRAW_CARD, NEW_CARD,

    STARDUST, SLAVERS , ABANDONEDSHIP, ABANDONEDSTATION, COMBATZONE, EPIDEMIC, METEORSWARM, OPENSPACE, PIRATES, PLANETS,
    SMUGGLERS,

    //la seguente riga e' poi da eliminare (vede Stefano)
    CREW_REMOVE_CHOICE,

    ENGINE_CHOICE, GOODS_PLACEMENT, DRILL_CHOICE,

    WAITING_ROLL, DICE_ROLL, FINISH_HOURGLASS, FINISH_LAST_HOURGLASS
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
