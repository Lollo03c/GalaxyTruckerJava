package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.Planet;

import java.util.List;

public final class SldPlanets extends SldAdvCard{
    private final int daysLost;
    private final List<Planet> planets;
    private List<Player> landedPlayers;

    public SldPlanets(int id, int level, int daysLost, List<Planet> planets) {
        super(id, level);
        this.daysLost = daysLost;
        this.planets = planets;
    }

    @Override
    public String getCardName() {
        return "Planets";
    }

    @Override
    public void init(FlyBoard board) {

    }

    @Override
    public void finish(FlyBoard board) {

    }
}
