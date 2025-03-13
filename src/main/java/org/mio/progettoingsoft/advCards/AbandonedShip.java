package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.FlyBoard;

public class AbandonedShip extends AdventureCard {
    private int daysLost;
    private int credits;
    private int crewLost;

    public AbandonedShip(int id, int level, int daysLost, int credits, int crewLost) {
        super(id, level, AdvCardType.ABANDONED_SHIP);
        this.daysLost = daysLost;
        this.credits = credits;
        this.crewLost = crewLost;
    }

    @Override
    public void start(FlyBoard board){

    }
}
