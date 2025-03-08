package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.components.GoodType;

import java.util.List;

public class AbandonedStation extends AdventureCard {
    private int daysLost;
    private int crewNeeded;
    private List<GoodType> goods;

    protected AbandonedStation(int id, int level) {
        super(id, level);
    }
}
