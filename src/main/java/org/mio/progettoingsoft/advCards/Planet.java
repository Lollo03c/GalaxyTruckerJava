package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.components.GoodType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Planet {
    private Optional<Player> player;
    private final List<GoodType> goods;

    public Planet(List<GoodType> goods) {
        this.goods = goods;
    }


    public void land(Player player){
        this.player = Optional.of(player);
    }
    public Optional<Player> getPlayer() {
        return player;
    }

    public static Planet stringToPlanet(JsonNode goodsNode){
        List<GoodType> goods = new ArrayList<>();

        for(JsonNode g : goodsNode){
            goods.add(GoodType.stringToGoodType(g.asText()));
        }

        return new Planet(goods);
    }
    public List<GoodType> getGoods() {
        return goods;
    }
}
