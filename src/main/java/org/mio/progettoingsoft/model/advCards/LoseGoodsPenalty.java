package org.mio.progettoingsoft.model.advCards;

import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Player;

public class LoseGoodsPenalty extends LoseSomethingPenalty {
    public LoseGoodsPenalty(int amount) {
        super(amount);
    }

    @Override
    public PenaltyType getType() {
        return PenaltyType.GOODS;
    }

    public void apply(FlyBoard board, Player player) {
//        player.getShipBoard().removeGoods(amount);
    }

}
