package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;

import java.util.List;

public class Planets extends AdventureCard {
    private final int daysLost;
    private final List<Planet> planets;


    public Planets(int id, int level, int daysLost, List<Planet> planets) {
        super(id, level);
        this.daysLost = daysLost;
        this.planets = planets;
    }
}
