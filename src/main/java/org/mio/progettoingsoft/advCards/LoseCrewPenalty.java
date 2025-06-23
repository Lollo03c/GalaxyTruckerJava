package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.model.events.Event;

import java.util.List;

public class LoseCrewPenalty extends LoseSomethingPenalty{
    public LoseCrewPenalty(int amount) {
        super(amount);
    }

    @Override
    public PenaltyType getType() {
        return PenaltyType.CREW;
    }

//    public void apply(FlyBoard board, Player player, List<Integer[]> housingToRemoveCrew){
//        Event event = new Set
//    }
}
