package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.responses.Response;
import org.mio.progettoingsoft.responses.SmugglersResponse;

import java.util.ArrayList;
import java.util.List;

public class Smugglers extends AdvancedEnemy {
    private final int stolenGoods;
    private final List<GoodType> goods;
    private final int strength;
    private final int daysLost;

    public Smugglers(int id, int level, int strength, int daysLost, int stolenGoods, List<GoodType> goods) {
        super(id, level, strength, daysLost, AdvCardType.SMUGGLERS);
        this.stolenGoods = stolenGoods;
        this.goods = goods;
        this.strength = strength;
        this.daysLost = daysLost;
    }
    
    public static Smugglers loadSmugglers(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<GoodType> rewards = new ArrayList<>();
        JsonNode rewardNode = node.path("reward");
        for (JsonNode reward : rewardNode) {
            rewards.add(GoodType.stringToGoodType(reward.asText()));
        }
        int goodsLost = node.path("goodsLost").asInt();

        return new Smugglers(id, level, strength, daysLost, goodsLost, rewards);
    }

    @Override
    public int getStrength(){
        return strength;
    }

    @Override
    public int getDaysLost(){
        return daysLost;
    }

    @Override
    public List<GoodType> getGoods(){
        return goods;
    }

    @Override
    public int getStolenGoods(){
        return stolenGoods;
    }

    @Override
    public void start(){
        playersToPlay = new ArrayList<>(flyBoard.getScoreBoard());

        iterator = playersToPlay.iterator();
        chooseNextPlayerState();
    }

    @Override
    public void applyEffect(String json) throws JsonProcessingException {
        SmugglersResponse response = objectMapper.readValue(json, SmugglersResponse.class);

        if (response.getColorPlayer().equals(playerState)){
            if (response.getStreght() > strength){
                ShipBoard shipBoard = playerState.getShipBoard();
                for (Integer position : response.getDepos().keySet()){
                    shipBoard.getComponent(position).setGoodsDepot(response.getDepos().get(position));
                }
                flyBoard.moveDays(playerState, -daysLost);
            }
            else if (response.getStreght() < strength){
                playerState.getShipBoard().stoleGood(stolenGoods);
                chooseNextPlayerState();
            }
            else{
                chooseNextPlayerState();
            }
        }
    }

}
