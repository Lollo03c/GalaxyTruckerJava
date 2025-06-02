package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.advCards.CannonPenalty;

import java.util.List;

public final class SldPirates extends SldAdvCard{
    private final int strength;
    private final int credits;
    private final List<CannonPenalty> cannons;
    private final int daysLost;
    public SldPirates(int id, int level, int daysLost, int strength, int credits, List<CannonPenalty> cannons) {
        super(id, level);
        this.strength = strength;
        this.credits = credits;
        this.daysLost = daysLost;
        this.cannons = cannons;
    }

    @Override
    public int getDaysLost() {return daysLost;}

    @Override
    public String getCardName() {
        return "Pirates";
    }

    @Override
    public void init(Game game) {
        FlyBoard board = game.getFlyboard();
    }

    @Override
    public int getCredits() {return credits;}

    @Override
    public int getStrength() {
        return strength;
    }

    @Override
    public List<CannonPenalty> getCannonPenalty(){
        return cannons;
    }

    @Override
    public void finish(FlyBoard board) {

    }
}
