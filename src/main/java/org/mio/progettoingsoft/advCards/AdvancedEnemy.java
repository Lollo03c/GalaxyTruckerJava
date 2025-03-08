package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;

public abstract class AdvancedEnemy extends AdventureCard {
    private final int strength;
    private final int daysLost;

    protected AdvancedEnemy(int id, int level, int strength, int daysLost) {
        super(id, level);
        this.strength = strength;
        this.daysLost = daysLost;
    }


}
