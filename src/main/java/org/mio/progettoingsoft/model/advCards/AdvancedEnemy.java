package org.mio.progettoingsoft.model.advCards;

public abstract class AdvancedEnemy extends AdventureCard {
    protected final int strength;
    protected final int daysLost;

    public AdvancedEnemy(int id, int level, int strength, int daysLost, AdvCardType advCardType) {
        super(id, level, advCardType);
        this.strength = strength;
        this.daysLost = daysLost;
    }

}
