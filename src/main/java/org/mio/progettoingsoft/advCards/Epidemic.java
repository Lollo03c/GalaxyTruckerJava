package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.AlienType;

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


    // still to be implemented: can a player choose which member to remove? alien o human?
    // as it is the method removes the human, if it doesn't find humans, it removes the alien (if present)

    // Antonio -> ho toleto removeHuman and removeAlien e sostituiti con removeGuest (+ generico)
    public void startTest(FlyBoard fly, Player player){
        player.getShipBoard().getComponentsStream()
                .filter(c -> c.getType().equals(ComponentType.HOUSING))
                .forEach(c -> {
                    Map<Direction, Component> adj = player.getShipBoard().getAdjacent(c.getRow(), c.getColumn());
                    adj.forEach((direction, component) -> {
                        if(component.getType().equals(ComponentType.HOUSING)){
                            component.removeGuest();
//                            if(!component.removeHumanMember()){
//                            if (!component.removeGuest())
//                                if(component.canContainsAlien(AlienType.BROWN))
//                                    component.removeAlien(AlienType.BROWN);
//                                else if(component.canContainsAlien(AlienType.PURPLE))
//                                    component.removeAlien(AlienType.PURPLE);
//                            }
                        }
                    });
                });
    }
}
