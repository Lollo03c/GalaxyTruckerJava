package org.mio.progettoingsoft.advcards;

import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.components.GoodType;

import java.util.List;

public class Smugglers extends AdventureCard {
    private int stolenGoods;
    private int daysLost;
    private int strength;
    private final List<GoodType> goods;

    protected Smugglers(int level, List<GoodType> goods) {
        super(level);
        this.goods = goods;
    }
}
