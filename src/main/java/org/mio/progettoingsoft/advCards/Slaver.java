package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;

import java.util.ArrayList;

public class Slaver extends AdvancedEnemy{
    private final int crewLost;
    private final int reward;

    @Override
    public int getCrewLost() {
        return crewLost;
    }

    public int getCredits() {
        return reward;
    }

    public Slaver(int id, int level, int strength, int daysLost, int reward, int crewLost) {
        super(id, level, strength, daysLost, AdvCardType.SLAVER);
        this.crewLost = crewLost;
        this.reward = reward;
    }
    
    public static Slaver loadSlaver(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        int reward = node.path("reward").asInt();
        int crewLost = node.path("crewLost").asInt();

        return new Slaver(id, level, strength, daysLost, reward, crewLost);
    }

    @Override
    public int getDaysLost() {
        return daysLost;
    }

    @Override
    public int getStrength(){
        return strength;
    }

    @Override
    public void start(){
        playersToPlay = new ArrayList<>(flyBoard.getScoreBoard());
        iterator = playersToPlay.iterator();
//        flyBoard.setState(GameState.CARD_EFFECT);
    }

    @Override
    public void applyEffect(String json) throws JsonProcessingException {
//        SlaverResponse response = objectMapper.readValue(json, SlaverResponse.class);
//
//        if (response.getColorPlayer().equals(playerState.getColor())){
//            if (response.getStength() > strength){
//                playerState.addCredits(reward);
//                flyBoard.moveDays(playerState, -daysLost);
//
//                flyBoard.setState(GameState.DRAW_CARD);
//            }
//            else if (response.getStength() < strength){
//                for (Component comp : response.getHousing()){
////                    comp.removeGuest();
//                }
//            }
//
//
//        }
    }


}
