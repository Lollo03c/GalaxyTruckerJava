package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.SetStateEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an "Open Space" adventure card in the game.
 * This card type primarily deals with ship movement based on engine power,
 * potentially leading to players being removed from the game if their ship
 * lacks sufficient power to move.
 * It extends {@link SldAdvCard} and provides specific logic for managing player movement
 * and handling players with no engine power.
 */
public final class SldOpenSpace extends SldAdvCard {
    private final List<Player> noPowerPlayers = new ArrayList<>();

    /**
     * Constructs a new {@code SldOpenSpace} adventure card.
     *
     * @param id The unique identifier for this open space card.
     * @param level The level of the open space, potentially influencing movement or penalties.
     */
    public SldOpenSpace(int id, int level) {
        super(id, level);
    }

    /**
     * Overrides the base method to return the name of this card.
     *
     * @return The string "Open Space".
     */
    public String getCardName() {
        return "Open Space";
    }

    /**
     * Static factory method to load an {@code SldOpenSpace} object from a JSON node.
     * It parses the node to extract ID and level, then constructs a new {@code SldOpenSpace} instance.
     *
     * @param node The {@link JsonNode} containing the Open Space card data.
     * @return A new {@code SldOpenSpace} instance populated with data from the JSON node.
     */
    public static SldOpenSpace loadOpenSpace(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new SldOpenSpace(id, level);
    }

    /**
     * Initializes the Open Space card's state and context within the game.
     * It sets up the game and fly board references, and initializes the list of allowed players
     * (all players on the fly board) and the player iterator.
     * The card state is not directly set here but is expected to transition to {@code ENGINE_CHOICE}
     * by {@code setNextPlayer()}.
     */
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

    /**
     * Applies the effect of the Open Space card for a specific player.
     * This method is called by each player to activate their ship's double engines
     * (by consuming batteries) and then move their ship forward by a number of days
     * equal to their total engine power.
     * Players with zero total engine power after activation are marked for removal.
     *
     * @param player The {@link Player} applying the effect.
     * @param numDoubleEngines The number of double engines the player wishes to activate.
     * @throws IncorrectFlyBoardException if the provided player is not found on the fly board or is not the current active player.
     * @throws IncorrectShipBoardException if the player does not have enough batteries, or the number of selected engines is invalid.
     */
    public void applyEffect(Player player, int numDoubleEngines) {
        if (!flyBoard.getScoreBoard().contains(player)) {
//            setNextPlayer();
//            return;
            throw new IncorrectFlyBoardException("player not found");
        }

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
            int power = base + (numDoubleEngines * 2);

            if (power == 0){
                noPowerPlayers.add(player);
            }

            flyBoard.moveDays(actualPlayer, power);
            setNextPlayer();

        } else {
            throw new IncorrectFlyBoardException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

    }

    /**
     * Sets the next player to apply the Open Space effect or finalizes the card.
     * If there are more players in the {@code allowedPlayers} list, the card state
     * transitions to {@code ENGINE_CHOICE}. Otherwise, it processes players
     * marked with no power by removing them from the fly board and
     * then sets the card state to {@code FINALIZED}.
     */
    @Override
    public void setNextPlayer(){
        if (playerIterator.hasNext()) {
            this.actualPlayer = this.playerIterator.next();
            setState(CardState.ENGINE_CHOICE);
        } else {
            for (Player player : noPowerPlayers){
                flyBoard.leavePlayer(player);
                Event event = new SetStateEvent(player.getNickname(), GameState.REMOVED_FROM_FLYBOARD);
                game.addEvent(event);
            }
            setState(CardState.FINALIZED);
        }
    }
}