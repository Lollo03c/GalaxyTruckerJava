package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.*;

import java.util.ArrayList;

public class OpenSpace extends AdventureCard {
    public OpenSpace(int id, int level) {
        super(id, level, AdvCardType.OPEN_SPACE);
    }

    public static OpenSpace loadOpenSpace(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new OpenSpace(id, level);
    }

    @Override
    public void start(){
        playersToPlay = new ArrayList<>(flyBoard.getScoreBoard());

        iterator = playersToPlay.iterator();
//        flyBoard.setState(GameState.CARD_EFFECT);
    }

    @Override
    public void applyEffect(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
//        OpenSpaceResponse response = objectMapper.readValue(json, OpenSpaceResponse.class);
//
//        if (response.getColorPlayer().equals(playerState.getColor())){
//            flyBoard.moveDays(playerState, response.getTotalEnginePower());
//        }
    }


}
