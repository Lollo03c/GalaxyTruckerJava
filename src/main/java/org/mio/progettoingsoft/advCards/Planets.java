package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Planets extends AdventureCard {
    private final int daysLost;
    private final List<Planet> planets;

    @Override
    public List<Planet> getPlanets() {
        return planets;
    }

    @Override
    public int getDaysLost() {
        return daysLost;
    }

    public Planets(int id, int level, int daysLost, List<Planet> planets) {
        super(id, level, AdvCardType.PLANETS);
        this.daysLost = daysLost;
        this.planets = planets;
    }

    public static Planets loadPlanets(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<Planet> planets = new ArrayList<>();
        JsonNode planetsNode = node.path("planets");
        for(JsonNode planet : planetsNode) {
            planets.add(Planet.stringToPlanet(planet));
        }

        return new Planets(id, level, daysLost, planets);
    }

    // Il metodo start è stato spostato nel controller come tutte le altre carte

}
