package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.advCards.AdvancedEnemy;

import java.util.List;

public class Pirate extends AdvancedEnemy{
    private final List<CannonPenalty> cannons;
    private final int reward;

    public Pirate(int id, int level, int strength, int daysLost, List<CannonPenalty> cannons, int reward) {
        super(id, level, strength, daysLost, AdvCardType.PIRATE);
        this.cannons = cannons;
        this.reward = reward;
    }
}
