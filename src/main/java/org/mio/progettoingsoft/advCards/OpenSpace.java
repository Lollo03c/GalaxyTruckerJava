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

    // apply the effect of OpenSpace card, the parameter "cod" is the number of double engine to give power to (already checked)
    // move ahead the player based on the base power and the tmp added power
    // if the ship of a player has no engine or has no single engine but cannot activate a double one, the method throws
    // a "NoPowerException", then the controller has to remove the player
    @Override
    public void startTest(FlyBoard fly, Player player, int cod){
        player.getShipBoard().removeEnergy(cod);
        int base = player.getShipBoard().getBaseEnginePower();
        int power = base + cod*2;
        if(power == 0){
            throw new NoPowerException(player);
        }else{
            fly.moveDays(player, power);
        }
    }
}
