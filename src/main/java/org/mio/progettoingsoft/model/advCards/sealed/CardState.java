package org.mio.progettoingsoft.model.advCards.sealed;

import java.io.Serializable;

/**
 * Enumerates the various possible states of a sealed advanced card during gameplay.
 * <p>
 * Each state defines a phase of card interaction logic, such as user decisions,
 * effect application, or special mechanics like dice rolling or shield selection.
 * These states are used to track the current progress and control flow when a sealed card is played.
 */
public enum CardState implements Serializable {
    CREW_REMOVE_CHOICE, ENGINE_CHOICE, DRILL_CHOICE, ACCEPTATION_CHOICE, COMPARING, FINALIZED, GOODS_PLACEMENT, APPLY_HEAVY_CANNON, APPLY_LIGHT_CANNON, APPLYING, PART_CHOICE, PART_REMOVING_DONE, REMOVE_GOODS, PLANET_CHOICE, IDLE, ERROR_CHOICE,
    DICE_ROLL, WAITING_ROLL, SHIELD_SELECTION, STARDUST_END, EPIDEMIC_END, ASK_ONE_DOUBLE_DRILL,
    METEOR_HIT, CANNON_HIT, ASK_LEAVE
}
