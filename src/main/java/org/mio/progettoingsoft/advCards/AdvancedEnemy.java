package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;

public abstract class AdvancedEnemy extends AdventureCard {
    private final int strength;
    private final int daysLost;

    public AdvancedEnemy(int id, int level, int strength, int daysLost, AdvCardType advCardType) {
        super(id, level, advCardType);
        this.strength = strength;
        this.daysLost = daysLost;
    }
}
