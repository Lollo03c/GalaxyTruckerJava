package org.mio.progettoingsoft.model.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.model.*;
import org.mio.progettoingsoft.model.enums.Direction;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveGuestEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for all sealed advanced cards used in the game.
 * <p>
 * This class defines shared behaviors and interfaces for cards that introduce advanced interactions
 * such as player choices, penalties, effects, and state transitions. Subclasses must implement
 * specific behavior, especially for card initialization and name retrieval.
 * <p>
 * All sealed cards are required to extend this class and are governed by a predefined set of states.
 */
public final class SldEpidemic extends SldAdvCard {
    /**
     * Constructs a new {@code SldEpidemic} adventure card.
     *
     * @param id The unique identifier for this epidemic card.
     * @param level The level of the epidemic, potentially indicating its severity or impact.
     */
    public SldEpidemic(int id, int level) {
        super(id, level);
    }

    /**
     * Returns the display name of the card.
     *
     * @return the card name
     */
    public String getCardName(){
        return "Epidemic";
    }

    /**
     * Loads an {@code SldEpidemic} object from a JSON node.
     * This static method parses the provided {@link JsonNode} to extract the necessary
     * attributes (id and level) and constructs a new {@code SldEpidemic} instance.
     *
     * @param node The {@link JsonNode} containing the data for the epidemic card.
     * @return A new {@code SldEpidemic} instance populated with data from the JSON node.
     */
    public static SldEpidemic loadEpidemic(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();

        return new SldEpidemic(id, level);
    }

    /**
     * Initializes the card, setting its internal game context and determining eligible players.
     * and removes the member from the {@link ShipBoard}
     * @param game the current game instance
     */
    @Override
    public void init(GameServer game){
        this.game = game;
        this.flyBoard = game.getFlyboard();

        actualPlayer = flyBoard.getScoreBoard().getFirst();

        for (Player player : flyBoard.getScoreBoard()){
            ShipBoard ship = player.getShipBoard();

            Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();
            Set<Component> toRemove = new HashSet<>();

            while (cordinateIterator.hasNext()){
                Cordinate cord = cordinateIterator.next();
                if (ship.getOptComponentByCord(cord).isEmpty())
                    continue;
                Component comp = ship.getOptComponentByCord(cord).get();

                Map<Direction, Component> ajdacents = ship.getAdjacent(cord);
                for (Direction dir : ajdacents.keySet()){
                    if (!comp.getGuests().isEmpty() && !ajdacents.get(dir).getGuests().isEmpty()){
                        toRemove.add(comp);
                        toRemove.add(ajdacents.get(dir));
                    }
                }
            }

            for (Component c : toRemove){
                c.removeGuest();

                Event removeGuestEvent = new RemoveGuestEvent(null, c.getId());
                game.addEvent(removeGuestEvent);
            }
        }

        setState(CardState.EPIDEMIC_END);
    }

    /**
     * Finalizes the card by setting its state to {@code FINALIZED}.
     */
    @Override
    public void setNextPlayer(){
        setState(CardState.FINALIZED);
    }
}
