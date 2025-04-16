package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.advCards.CannonPenalty;

import java.util.List;

public final class SldPirates extends SldAdvCard{
    private final int strength;
    private final int credits;
    private final List<CannonPenalty> cannons;
    public SldPirates(int id, int level, int strength, int credits, List<CannonPenalty> cannons) {
        super(id, level);
        this.strength = strength;
        this.credits = credits;
        this.cannons = cannons;
    }

    @Override
    public String getCardName() {
        return "Pirates";
    }

    @Override
    public void init(FlyBoard board) {

    }

    @Override
    public void finish(FlyBoard board) {

    }
}
