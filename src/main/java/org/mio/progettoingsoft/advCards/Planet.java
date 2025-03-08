package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.components.GoodType;

import java.util.List;
import java.util.Optional;

public class Planet {
    private Optional<Player> player;
    private final List<GoodType> goods;

    public Planet(List<GoodType> goods) {
        this.goods = goods;
    }

    public void land(Player player){

    };
}
