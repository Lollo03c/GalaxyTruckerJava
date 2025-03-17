package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Player;

public abstract class AdvancedEnemy extends AdventureCard {
    private final int strength;
    private final int daysLost;

    public AdvancedEnemy(int id, int level, int strength, int daysLost, AdvCardType advCardType) {
        super(id, level, advCardType);
        this.strength = strength;
        this.daysLost = daysLost;
    }

    @Override
    public boolean canBeDefeatedBy(Player player, int cod) {
        double base = player.getShipBoard().getBaseFirePower();
        double power = base + player.getShipBoard().getComponentsStream()
                .filter(c -> c.getType().equals(ComponentType.DOUBLE_DRILL) && c.isActive())
                .mapToDouble(c -> c.getFirePower())
                .sum();
        return power > strength;
    }
}
