package org.mio.progettoingsoft.advcards;

import org.mio.progettoingsoft.advcards.AdvancedEnemy;

import java.util.List;

public class Pirate extends AdvancedEnemy{
    private List<CannonPenalty> cannons;

    protected Pirate(int level, int strength, int daysLost, int reward) {
        super(level, strength, daysLost, reward);
    }
}
