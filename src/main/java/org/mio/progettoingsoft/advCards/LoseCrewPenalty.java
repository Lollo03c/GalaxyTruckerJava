package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;

import java.util.List;

public class LoseCrewPenalty extends LoseSomethingPenalty{
    public LoseCrewPenalty(int amount) {
        super(amount);
    }

    @Override
    public PenaltyType getType() {
        return PenaltyType.CREW;
    }

    public void apply(FlyBoard board, Player player, List<Integer[]> housingToRemoveCrew){
        for (Integer[] integers : housingToRemoveCrew) {
            int row = integers[0];
            int col = integers[1];
//            board.getPlayerByUsername(player.getNickname()).get().getShipBoard().getComponent(row, col).removeGuest();
        }
    }
}
