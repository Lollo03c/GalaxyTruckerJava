package org.mio.progettoingsoft.model.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.List;

/**
 * Represents a "Stardust" adventure card in the game.
 * This card type causes players to lose days based on the number of exposed connectors on their ship,
 * simulating the detrimental effect of stardust on unprotected ship components.
 * It extends {@link SldAdvCard} and provides specific logic for stardust events.
 */
public final class SldStardust extends SldAdvCard{
    /**
     * Constructs a new {@code SldStardust} adventure card.
     *
     * @param id The unique identifier for this stardust card.
     * @param level The level of the stardust event, potentially indicating its severity.
     */
    public SldStardust(int id, int level) {
        super(id, level);
    }

    /**
     * Overrides the base method to return the name of this card.
     *
     * @return The string "Stardust".
     */
    @Override
    public String getCardName() {
        return "Stardust";
    }

    /**
     * Static factory method to load an {@code SldStardust} object from a JSON node.
     * It parses the node to extract ID and level, then constructs a new {@code SldStardust} instance.
     *
     * @param node The {@link JsonNode} containing the Stardust card data.
     * @return A new {@code SldStardust} instance populated with data from the JSON node.
     */
    public static SldStardust loadStardust(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new SldStardust(id, level);
    }

    /**
     * Initializes the Stardust card's state and context within the game.
     * It sets up game and fly board references.
     * This card's primary effect is applied immediately upon initialization:
     * all players, in reversed order of the scoreboard, lose days equal to their ship's exposed connectors.
     * The card then transitions directly to the {@code STARDUST_END} state, signifying its immediate effect.
     *
     * @param game The {@link GameServer} instance managing the current game.
     */
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

        setState(CardState.STARDUST_END);
    }

    /**
     * Overrides the base method to set the card state to {@code FINALIZED}.
     * For the Stardust card, the effect is applied immediately in {@code init},
     * so this method simply transitions to the final state.
     */
    @Override
    public void setNextPlayer() {
        setState(CardState.FINALIZED);
    }
}
