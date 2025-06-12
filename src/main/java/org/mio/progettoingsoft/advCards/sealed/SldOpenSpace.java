package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.OpenSpace;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.NotEnoughBatteriesException;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.List;

public final class SldOpenSpace extends SldAdvCard {
    private List<Player> noPowerPlayers;

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

        noPowerPlayers = flyBoard.getScoreBoard().stream()
                .filter(player ->
                        player.getShipBoard().getBaseEnginePower() == 0 &&
                                (player.getShipBoard().getDoubleEngine().isEmpty() ||
                                        player.getShipBoard().getQuantBatteries() <= 0)
                ).toList();
        // allowedPlayers is a new list because the score board will be modified by the applyEffect
        this.allowedPlayers = new ArrayList<>(flyBoard.getScoreBoard());
        allowedPlayers.removeAll(noPowerPlayers);
        this.playerIterator = allowedPlayers.iterator();
    }

    // must be called right after init with the right player
    // starting from the leader, it activates the double engines (if possible) and moves the player
    // if the player is the last, it sets the card state to FINALIZED (to accept only finish calls)
    // else, it sets the card state to ENGINE_CHOICE to accept other calls with next players
    public void applyEffect(Player player, int numDoubleEngines) {
        if (this.state != CardState.ENGINE_CHOICE) {
            throw new IllegalStateException("The effect can't be applied or has been already applied: " + this.state);
        }
        if (!flyBoard.getScoreBoard().contains(player)) {
            throw new BadPlayerException("The player " + player.getNickname() + " is not in the board");
        }

        this.state = CardState.APPLYING;

        if (player.equals(actualPlayer)) {
            if (numDoubleEngines > actualPlayer.getShipBoard().getQuantBatteries()) {
                throw new NotEnoughBatteriesException();
            }
            if (numDoubleEngines < 0) {
                throw new BadParameterException("The number of selected engines is less than zero");
            }
            if (numDoubleEngines > actualPlayer.getShipBoard().getDoubleEngine().size()) {
                throw new BadParameterException("The number of selected engines is more than the actual engines");
            }
            player.getShipBoard().removeEnergy(numDoubleEngines);
            int base = player.getShipBoard().getBaseEnginePower();
            int power = base + numDoubleEngines * 2;

            flyBoard.moveDays(actualPlayer, power);

        } else {
            throw new BadPlayerException("The player " + actualPlayer.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

    }

    // removes the players with no power
    public void finish(FlyBoard board) {
        if (this.state != CardState.FINALIZED) {
            throw new IllegalStateException("Illegal state for 'finish': " + this.state);
        }
        if (!noPowerPlayers.isEmpty()) {
            // here the method should call a procedure or throw an exception to remove the players with no power
            throw new RuntimeException("There's at least a player with no power, not implemented yet");
        }
//        board.setState(GameState.DRAW_CARD);
    }

    @Override
    public void setNextPlayer(){
        if (playerIterator.hasNext()) {
            setState(CardState.ENGINE_CHOICE);
        } else {
            setState(CardState.FINALIZED);
        }
    }
}
