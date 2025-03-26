package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.exceptions.BadCardException;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.responses.OpenSpaceResponse;
import org.mio.progettoingsoft.responses.Response;

import java.util.ArrayList;

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
    public void applyEffect(Response res){
        OpenSpaceResponse response = (OpenSpaceResponse) res;

        Player player = flyBoard.getPlayerByColor(res.getColorPlayer()).get();
        flyBoard.moveDays(player, -response.getTotalEnginPower());
    }


}
