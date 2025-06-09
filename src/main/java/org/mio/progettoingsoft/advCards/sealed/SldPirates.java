package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.Pirate;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
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

    public static SldPirates loadPirate(JsonNode node){
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

        return new SldPirates(id, level, daysLost, strength, reward, cannons);
    }

    @Override
    public int getDaysLost() {return daysLost;}

    @Override
    public String getCardName() {
        return "Pirates";
    }

    @Override
    public void init(GameServer game) {
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
