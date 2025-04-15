package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;

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
