package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.AlienType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

public class Epidemic extends AdventureCard {
    public Epidemic(int id, int level) {
        super(id, level, AdvCardType.EPIDEMIC);
    }

    public static Epidemic loadEpidemic(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new Epidemic(id, level);
    }

    // this method apply the effect of the card on the passed player: it removes one guest (human or alien) from every
    // non-empty housing connected to another non-empty housing
    public void startTest(FlyBoard fly, Player player, int cod){
        List<Component> toDoRemove = new ArrayList<>();
        player.getShipBoard().getComponentsStream()
                .filter(c -> c.getType().equals(ComponentType.HOUSING))
                .forEach(c -> {
                    Map<Direction, Component> adj = player.getShipBoard().getAdjacent(c.getRow(), c.getColumn());
                    adj.forEach((direction, component) -> {
                        if(component.getType().equals(ComponentType.HOUSING) && c.getQuantityGuests() > 0 && component.getQuantityGuests() > 0){
                            toDoRemove.add(component);
                        }
                    });
                });
        for(Component c : toDoRemove){
            c.removeGuest();
        }
    }
}
