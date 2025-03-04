package org.mio.progettoingsoft.advcards;

import org.mio.progettoingsoft.AdventureCard;

public class AbandonedShip extends AdventureCard {
    private int daysLost;
    private int credits;
    private int crewLost;

    public AbandonedShip(int level, int daysLost, int credits, int crewLost) {
        super(level);
        this.daysLost = daysLost;
        this.credits = credits;
        this.crewLost = crewLost;
    }
}
