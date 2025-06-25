package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.ShipBoardNormal;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public final class SldOpenSpace extends SldAdvCard {
    private final List<Player> noPowerPlayers = new ArrayList<>();

    public SldOpenSpace(int id, int level) {
        super(id, level);
    }

    public static SldOpenSpace loadOpenSpace(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new SldOpenSpace(id, level);
    }

    public String getCardName() {
        return "Open Space";
    }

    // identifies all the players with no power (only the ones with no engines and no double engines/no batteries to
    // activate them), they will be removed by the finish method (still to be implemented)
    // sets the card state to ENGINE_CHOICE (to accept calls by the players)
    public void init(GameServer game) {
//        if (board.getState() != GameState.DRAW_CARD) {
//            throw new IllegalStateException("Illegal state: " + board.getState());
//        }
        this.game = game;
        this.flyBoard = game.getFlyboard();

        // allowedPlayers is a new list because the score board will be modified by the applyEffect
        this.allowedPlayers = new ArrayList<>(flyBoard.getScoreBoard());
//        allowedPlayers.removeAll(noPowerPlayers);
        this.playerIterator = allowedPlayers.iterator();
    }

    // must be called right after init with the right player
    // starting from the leader, it activates the double engines (if possible) and moves the player
    // if the player is the last, it sets the card state to FINALIZED (to accept only finish calls)
    // else, it sets the card state to ENGINE_CHOICE to accept other calls with next players
    public void applyEffect(Player player, int numDoubleEngines) {
        if (!flyBoard.getScoreBoard().contains(player)) {
            throw new IncorrectFlyBoardException("player not found");
        }

        this.state = CardState.APPLYING;

        if (player.equals(actualPlayer)) {
            if (numDoubleEngines > actualPlayer.getShipBoard().getQuantBatteries()) {
                throw new IncorrectShipBoardException("not enough batteries");
            }
            if (numDoubleEngines < 0) {
                throw new IncorrectShipBoardException("The number of selected engines is less than zero");
            }
            if (numDoubleEngines > actualPlayer.getShipBoard().getDoubleEngine().size()) {
                throw new IncorrectShipBoardException("The number of selected engines is more than the actual engines");
            }
            player.getShipBoard().removeEnergy(numDoubleEngines);
            int base = player.getShipBoard().getBaseEnginePower();
            int power = base + numDoubleEngines * 2;

            if (base == 0){
                noPowerPlayers.add(player);
            }

            flyBoard.moveDays(actualPlayer, power);
            setNextPlayer();

        } else {
            throw new IncorrectFlyBoardException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

    }

    @Override
    public void setNextPlayer(){
        if (playerIterator.hasNext()) {
            this.actualPlayer = this.playerIterator.next();
            setState(CardState.ENGINE_CHOICE);
        } else {
            for (Player player : noPowerPlayers){
                flyBoard.leavePlayer(player);
            }
            setState(CardState.FINALIZED);
        }
    }
}
