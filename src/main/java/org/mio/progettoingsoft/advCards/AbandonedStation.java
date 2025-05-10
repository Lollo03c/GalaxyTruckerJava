package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.responses.AbandonedStatationResponse;

import java.util.ArrayList;
import java.util.List;

public class AbandonedStation extends AdventureCard {
    private int daysLost;
    private int crewNeeded;
    private List<GoodType> goods;

    public AbandonedStation(int id, int level, List<GoodType> goods) {
        super(id, level, AdvCardType.ABANDONED_STATION);
        this.goods = goods;
    }
    
    public static AbandonedStation loadAbandonedStation(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        int crewNeeded = node.path("crewNeeded").asInt();
        List<GoodType> goods = new ArrayList<>();
        JsonNode goodsNode = node.path("goods");
        for (JsonNode good : goodsNode) {
            goods.add(GoodType.stringToGoodType(good.asText()));
        }
        
        return new AbandonedStation(id, level, goods);
    }

    public int getCrewNeeded(){
        return crewNeeded;
    }

    public int getDaysLost(){
        return daysLost;
    }

    public List<GoodType> getGoods(){
        return goods;
    }

    @Override
    public void start(){
        playersToPlay = flyBoard.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() >= crewNeeded)
                .toList();

        iterator = playersToPlay.iterator();
        flyBoard.setState(GameState.CARD_EFFECT);
    }

    @Override
    public void applyEffect(String json) throws JsonProcessingException {
        playerState = iterator.next();

        ObjectMapper objectMapper = new ObjectMapper();
        AbandonedStatationResponse response = objectMapper.readValue(json, AbandonedStatationResponse.class);

        if (response.getColorPlayer().equals(playerState.getColor())) {
            if (response.isAcceptEffect()) {
                ShipBoard shipBoard = playerState.getShipBoard();

                for (int i : response.getDepos().keySet()) {
//                    int[] pos = shipBoard.getCordinate(i);

//                    shipBoard.getComponent(pos[0], pos[1]).setGoodsDepot(response.getDepos().get(i));
                }
                flyBoard.moveDays(playerState, -daysLost);

                flyBoard.setState(GameState.DRAW_CARD);
            }
        }
    }

}
