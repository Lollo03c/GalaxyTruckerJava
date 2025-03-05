package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.AdventureCard;

import java.util.List;

public class Planets extends AdventureCard {
    private int daysLost;
    private final List<Planet> planets;


    public Planets(int level, int daysLost, List<Planet> planets) {
        super(level);
        this.daysLost = daysLost;
        this.planets = planets;
    }
}
