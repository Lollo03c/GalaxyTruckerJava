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
        player.getShipBoard().removeGoods(amount);
    }

}
