package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.responses.PirateResponse;

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

    @Override
    public int getCredits(){
        return  reward;
    }

    @Override
    public int getDaysLost(){
        return daysLost;
    }

    @Override
    public int getStrength(){
        return strength;
    }

    @Override
    public List<CannonPenalty> getCannonPenalty(){
        return cannons;
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

    @Override
    public void start(){
        playersToPlay = new ArrayList<>(flyBoard.getScoreBoard());
        iterator = playersToPlay.iterator();
        flyBoard.setState(GameState.CARD_EFFECT);
    }

    @Override
    public void applyEffect(String json) throws JsonProcessingException {
        PirateResponse response = objectMapper.readValue(json, PirateResponse.class);

        if (response.getColorPlayer().equals(playerState.getColor())){
            if (response.getStrength() > strength){
                playerState.getShipBoard().removeEnergy(response.getEnergyUsed());

                playerState.addCredits(reward);
                flyBoard.moveDays(playerState, -daysLost);
            }
            else if (response.getStrength() < strength){
                flyBoard.setState(GameState.CANNONS);
            }
        }
    }
}
