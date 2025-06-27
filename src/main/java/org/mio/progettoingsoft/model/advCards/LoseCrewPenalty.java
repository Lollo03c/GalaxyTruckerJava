package org.mio.progettoingsoft.model.advCards;

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
