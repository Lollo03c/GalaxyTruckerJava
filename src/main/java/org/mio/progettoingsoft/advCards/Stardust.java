package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;

import java.util.ArrayList;
import java.util.Collections;

public class Stardust extends AdventureCard {
    public Stardust(int id, int level) {
        super(id,level, AdvCardType.STARDUST);
    }

    public static Stardust loadStardust(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new Stardust(id, level);
    }

    @Override
    public void start(){
        playersToPlay = new ArrayList<>(flyBoard.getScoreBoard());
        Collections.reverse(playersToPlay);

        iterator = playersToPlay.iterator();
        flyBoard.setState(GameState.CARD_EFFECT);
    }

    public void applyEffect(String json){
        int daysLost = playerState.getShipBoard().getExposedConnectors();
        flyBoard.moveDays(playerState, -daysLost);
    }


}
