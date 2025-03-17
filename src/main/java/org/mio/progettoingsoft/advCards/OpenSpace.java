package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.exceptions.NoPowerException;

import java.util.ArrayList;
import java.util.List;

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
        List<Player> playerList = new ArrayList<>(board.getScoreBoard());
        for (Player player : playerList){
            int activated = player.getView().askDoubleEngine();
            player.getShipBoard().removeEnergy(activated);

            board.moveDays(player, player.getShipBoard().getBaseEnginePower() + 2 * activated);
        }
    }

}
