package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.responses.AbandonedShipResponse;
import org.mio.progettoingsoft.responses.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public void start(){
        playersToPlay = flyBoard.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() >= crewLost)
                .toList();

        iterator = playersToPlay.iterator();
        flyBoard.setState(StateEnum.CARD_EFFECT);
    }

    @Override
    public void applyEffect(String json) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        AbandonedShipResponse response = objectMapper.readValue(json, AbandonedShipResponse.class);

        if (response.getColorPlayer().equals(playerState.getColor())) {
            if (response.isAcceptEffect()) {
                ShipBoard shipBoard = flyBoard.getPlayerByColor(response.getColorPlayer()).get().getShipBoard();

                for (int i : response.getCrewDeleted()) {
                    int[] cord = shipBoard.getCordinate(i);
                    shipBoard.getComponent(cord[0], cord[1]).removeGuest();


                }
                flyBoard.moveDays(playerState, -daysLost);
                playerState.addCredits(credits);

                flyBoard.setState(StateEnum.DRAW_CARD);
            }

        }
    }
}
