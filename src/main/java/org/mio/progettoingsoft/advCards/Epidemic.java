package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;

public class Epidemic extends AdventureCard {
    public Epidemic(int id, int level) {
        super(id, level, AdvCardType.EPIDEMIC);
    }

    public static Epidemic loadEpidemic(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new Epidemic(id, level);
    }

}
