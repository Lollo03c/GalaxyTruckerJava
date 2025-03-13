package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;

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
    public void start(FlyBoard board){
        this.controller.ControllerCards(this, board);
    }
}
