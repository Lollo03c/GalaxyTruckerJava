package org.mio.progettoingsoft.model.advCards;

import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Player;

public class LoseDaysPenalty extends LoseSomethingPenalty{
    public LoseDaysPenalty(int amount) {
        super(amount);
    }

    @Override
    public PenaltyType getType() {
        return PenaltyType.DAYS;
    }

    public void apply(FlyBoard board, Player player) {
        board.moveDays(player, -amount);
    }
}
