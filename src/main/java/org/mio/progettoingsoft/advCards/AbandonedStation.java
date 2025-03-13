package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.components.GoodType;

import java.util.List;

public class AbandonedStation extends AdventureCard {
    private int daysLost;
    private int crewNeeded;
    private List<GoodType> goods;

    public AbandonedStation(int id, int level, List<GoodType> goods) {
        super(id, level, AdvCardType.ABANDONED_STATION);
        this.goods = goods;
    }
}
