package org.mio.progettoingsoft.model.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.model.Player;

import org.mio.progettoingsoft.model.components.GoodType;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.SetCardStateEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the sealed advanced card "Abandoned Station".
 * <p>
 * This card offers players a choice: lose a certain number of days and in return receive a predefined
 * set of goods. Only players with a minimum number of crew can activate its effect.
 * Once a player chooses to apply the effect, the card transitions to a placement state for rewards.
 */
public final class SldAbandonedStation extends SldAdvCard {
    private final int daysLost;
    private final int crewNeeded;
    private final List<GoodType> goods;

    private boolean effectTaken = false;

    /**
     * Constructs an Abandoned Station card with the given parameters.
     *
     * @param id the unique ID of the card
     * @param level the difficulty or tier level of the card
     * @param daysLost number of days a player loses if the effect is applied
     * @param crewNeeded number of crew members required to activate the effect
     * @param goods the list of goods rewarded upon applying the effect
     */
    public SldAbandonedStation(int id, int level, int daysLost, int crewNeeded, List<GoodType> goods) {
        super(id, level);
        this.daysLost = daysLost;
        this.crewNeeded = crewNeeded;
        this.goods = goods;
    }

    /**
     * Loads an {@code SldAbandonedStation} object from a JSON node.
     * This static method parses the provided {@link JsonNode} to extract the necessary
     * attributes (id, level, daysLost, crewNeeded, and a list of goods) and
     * constructs a new {@code SldAbandonedStation} instance.
     *
     * @param node The {@link JsonNode} containing the data for the abandoned station.
     * @return A new {@code SldAbandonedStation} instance populated with data from the JSON node.
     */
    public static SldAbandonedStation loadAbandonedStation(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        int crewNeeded = node.path("crewNeeded").asInt();
        List<GoodType> goods = new ArrayList<>();
        JsonNode goodsNode = node.path("goods");
        for (JsonNode good : goodsNode) {
            goods.add(GoodType.stringToGoodType(good.asText()));
        }

        return new SldAbandonedStation(id, level,  daysLost,crewNeeded, goods);
    }

    /**
     * Retrieves the list of goods found at this abandoned station.
     *
     * @return A list of {@link GoodType} representing the goods.
     */
    @Override
    public List<GoodType> getGoods(){
        return goods;
    }

    /**
     * Retrieves the number of days lost due to interacting with this abandoned station.
     *
     * @return The number of days lost.
     */
    @Override
    public int getDaysLost() {return daysLost;}

    /**
     * Retrieves the number of crew members needed to interact with this abandoned station.
     *
     * @return The number of crew members required.
     */
    @Override
    public int getCrewNeeded(){
        return crewNeeded;
    }

    /**
     * Retrieves the name of this card.
     *
     * @return The string "Abandoned Station".
     */
    @Override
    public String getCardName() {
        return "Abandoned Station";
    }

    /**
     * Initializes the card by determining which players are eligible based on their crew count.
     *
     * @param game the game server instance
     */
    @Override
    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();

        allowedPlayers = flyBoard.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() >= crewNeeded)
                .toList();
        playerIterator = allowedPlayers.iterator();
        effectTaken = false;
    }

    /**
     * Applies the card effect for the given player, if they accept.
     * <p>
     * The player loses days, and the game pushes an event to allow goods placement.
     *
     * @param player the player activating the card
     * @param wantsToApply true if the player agrees to apply the effect
     * @throws IllegalStateException if the card is not in an appropriate state
     */
    public void applyEffect(Player player, boolean wantsToApply) {
        if (this.state != CardState.ACCEPTATION_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }

        if (actualPlayer.equals(player)) {
            if (wantsToApply) {
                effectTaken = true;
                flyBoard.moveDays(actualPlayer, -daysLost);

                Event event = new SetCardStateEvent(player.getNickname(), CardState.GOODS_PLACEMENT);
                game.addEvent(event);
            }
        }

    }

    /**
     * Advances to the next player allowed to use the card, or finalizes it if complete.
     */
    @Override
    public void setNextPlayer(){
        if (playerIterator.hasNext() && !effectTaken) {
            actualPlayer = playerIterator.next();
            setState(CardState.ACCEPTATION_CHOICE);
        } else {
            effectTaken = false;
            setState(CardState.FINALIZED);
        }
    }
}
