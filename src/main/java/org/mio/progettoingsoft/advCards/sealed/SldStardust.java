package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.Stardust;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SldStardust extends SldAdvCard{
    public SldStardust(int level, int id) {
        super(level, id);
    }
    @Override
    public String getCardName() {
        return "Stardust";
    }

    public static SldStardust loadStardust(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new SldStardust(level, id);
    }

    @Override
    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();

        List<Player> inverserPlayers = flyBoard.getScoreBoard().reversed();
        playerIterator = inverserPlayers.iterator();
        actualPlayer = playerIterator.next();

        for (Player player : inverserPlayers){
            int daysLost = player.getShipBoard().getExposedConnectors();

            Logger.debug(player.getNickname() + " days lost : " + daysLost);
            flyBoard.moveDays(player, -daysLost);
        }

        setState(CardState.FINALIZED);


    }

    public void applyEffect(FlyBoard board) {
        /*if(this.state != CardState.APPLYING){
            throw new IllegalStateException("Illegal state: " + this.state);
        }*/
        List<Player> playersReverse = new ArrayList<>(board.getScoreBoard());
        Collections.reverse(playersReverse);
        for (Player player : playersReverse) {
            int daysLost = player.getShipBoard().getExposedConnectors();
            board.moveDays(player, -daysLost);
        }
        this.state = CardState.FINALIZED;
    }

    @Override
    public void finish(FlyBoard board) {
        if(this.state != CardState.FINALIZED){
            throw new IllegalStateException("Illegal state: " + this.state);
        }
//        board.setState(GameState.DRAW_CARD);
    }



}
