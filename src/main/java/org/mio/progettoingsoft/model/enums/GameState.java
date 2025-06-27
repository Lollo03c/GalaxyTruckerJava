package org.mio.progettoingsoft.model.enums;

import java.io.Serializable;

/**
 * Enumerates the various states that the game can be in.
 * These states control the flow of the game, from setup to gameplay,
 * and define specific actions or UI presentations.
 * The states are designed to manage the complexity of a turn-based game loop.
 */
public enum GameState implements Serializable {
    WAITING, START, NICKNAME, GAME_MODE, WAITING_PLAYERS, IDLE,

    GAME_START, BUILDING_SHIP, COMPONENT_MENU, ADD_COMPONENT, DRAW_UNCOVERED_COMPONENTS,
    VIEW_SHIP_BUILDING, VIEW_DECKS_LIST, VIEW_DECK, SWITCH_BOOKED, END_BUILDING, VALIDATION, CHOOSE_POSITION, VIEW_BOOKED, CHOICE_BUILT,

    ERROR_NICKNAME, ERROR_PLACEMENT, UNABLE_UNCOVERED_COMPONENT, UNABLE_DECK, WRONG_POSITION, INVALID_SHIP_CHOICE,

    ADVENTURE_STARTED, DRAW_CARD, CARD_EFFECT, YOU_CAN_DRAW_CARD, NEW_CARD,

    //la seguente riga e' poi da eliminare (vede Stefano)
    CREW_REMOVE_CHOICE,

    ENGINE_CHOICE, DRILL_CHOICE,

    WAITING_ROLL, DICE_ROLL, FINISH_HOURGLASS, FINISH_LAST_HOURGLASS,
    ASSIGN__REWARDS, STARTED_HOURGLASS, ENDGAME, YOU_CAN_ROTATE_HOURGLASS,
    ADD_CREW, REMOVED_FROM_FLYBOARD,

    PLAYER_REMOVED, GAME_CRASH
}
