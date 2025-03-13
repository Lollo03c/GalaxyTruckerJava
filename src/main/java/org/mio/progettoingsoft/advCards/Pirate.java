package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.advCards.AdvancedEnemy;

import java.util.ArrayList;
import java.util.List;

public class Pirate extends AdvancedEnemy{
    private final List<CannonPenalty> cannons;
    private final int reward;

    public Pirate(int id, int level, int strength, int daysLost, List<CannonPenalty> cannons, int reward) {
        super(id, level, strength, daysLost, AdvCardType.PIRATE);
        this.cannons = cannons;
        this.reward = reward;
    }
    
    public static Pirate loadPirate(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<CannonPenalty> cannons = new ArrayList<>();
        JsonNode cannonsNode = node.path("cannons");
        for (JsonNode cannon : cannonsNode) {
            cannons.add(CannonPenalty.stringToCannonPenalty(cannon.get(1).asText(),cannon.get(0).asText()));
        }
        int reward = node.path("reward").asInt();

        return new Pirate(id, level, strength, daysLost, cannons, reward);
    }
}
