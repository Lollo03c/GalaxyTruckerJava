package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.GoodType;

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

}
