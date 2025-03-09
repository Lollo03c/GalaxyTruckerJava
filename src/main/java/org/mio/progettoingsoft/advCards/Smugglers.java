package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.components.GoodType;

import java.util.List;

public class Smugglers extends AdvancedEnemy {
    private final int stolenGoods;
    private final List<GoodType> goods;

    public Smugglers(int id, int level, int strength, int daysLost, int stolenGoods, List<GoodType> goods) {
        super(id, level, strength, daysLost);
        this.stolenGoods = stolenGoods;
        this.goods = goods;
    }
}
