package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.responses.AbandonedShipResponse;
import org.mio.progettoingsoft.responses.Response;

import java.util.ArrayList;
import java.util.List;

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
    public int getDaysLost(){
        return  daysLost;
    }

    @Override
    public int getCredits(){
        return credits;
    }

    @Override
    public int getCrewLost(){
        return crewLost;
    }

    public void applyEffect(Response res){
        AbandonedShipResponse response = (AbandonedShipResponse) res;

        ShipBoard shipBoard = flyBoard.getPlayerByColor(res.getColorPlayer()).get().getShipBoard();

        if (response.isAcceptEffect()) {
            for (int i : response.getCrewDeleted()) {
                int[] cord = shipBoard.getCordinate(i);
                shipBoard.getComponent(cord[0], cord[1]).removeGuest();
            }
        }

    }
}
