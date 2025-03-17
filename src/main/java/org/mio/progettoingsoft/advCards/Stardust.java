package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Stardust extends AdventureCard {
    public Stardust(int id, int level) {
        super(id,level, AdvCardType.STARDUST);
    }

    public static Stardust loadStardust(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new Stardust(id, level);
    }

}
