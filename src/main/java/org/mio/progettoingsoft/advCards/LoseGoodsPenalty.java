package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.components.GoodType;

import java.util.List;

public class LoseGoodsPenalty extends LoseSomethingPenalty {
    public LoseGoodsPenalty(int amount) {
        super(amount);
    }

    @Override
    public PenaltyType getType() {
        return PenaltyType.GOODS;
    }

    public void apply(FlyBoard board, Player player) {
        GoodType nowRemoving = GoodType.RED;
        int i = amount;
        List<Component> depots = player.getShipBoard().getComponentsStream()
                .filter(c -> c.getType() == ComponentType.DEPOT)
                .toList();
        for (Component component : depots) {
            while (i > 0) {
                if (component.removeGood(nowRemoving)) i--;
                else break;
            }
        }
        if (i > 0) {
            nowRemoving = GoodType.YELLOW;
            for (Component component : depots) {
                while (i > 0) {
                    if (component.removeGood(nowRemoving)) i--;
                    else break;
                }
            }
            if (i > 0) {
                nowRemoving = GoodType.GREEN;
                for (Component component : depots) {
                    while (i > 0) {
                        if (component.removeGood(nowRemoving)) i--;
                        else break;
                    }
                }
                if (i > 0) {
                    nowRemoving = GoodType.BLUE;
                    for (Component component : depots) {
                        while (i > 0) {
                            if (component.removeGood(nowRemoving)) i--;
                            else break;
                        }
                    }
                    if (i > 0) {
                        while (i > 0) {
                            player.getShipBoard().removeEnergy(i);
                        }
                    }
                }
            }
        }
    }

}
