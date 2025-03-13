package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.FlyBoard;

public class AbandonedShip extends AdventureCard {
    private int daysLost;
    private int credits;
    private int crewLost;

    public AbandonedShip(int id, int level, int daysLost, int credits, int crewLost) {
        super(id, level, AdvCardType.ABANDONED_SHIP);
        this.daysLost = daysLost;
        this.credits = credits;
        this.crewLost = crewLost;
    }
    
    public static AbandonedShip loadAbandonedShip(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        int credits = node.path("credits").asInt();
        int crewLost = node.path("crewLost").asInt();
        
        return new AbandonedShip(id, level, daysLost, credits, crewLost);
    }

    @Override
    public void start(FlyBoard board){

    }
}
