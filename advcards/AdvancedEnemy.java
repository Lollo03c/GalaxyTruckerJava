package org.mio.progettoingsoft.advcards;

import org.mio.progettoingsoft.AdventureCard;

public abstract class AdvancedEnemy extends AdventureCard {
    private int strength;
    private int daysLost;
    private int reward;

    protected AdvancedEnemy(int level, int strength, int daysLost, int reward) {
        super(level);
        this.strength = strength;
        this.daysLost = daysLost;
        this.reward = reward;
    }


}
