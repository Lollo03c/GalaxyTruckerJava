package org.mio.progettoingsoft.model.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.model.Cordinate;
import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveGuestEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;

import java.util.List;

/**
 * Represents the sealed advanced card "Abandoned Ship", a special event card
 * that affects players by potentially costing crew members in exchange for credits and movement penalties.
 * <p>
 * This card may be played by players who have enough crew, allowing them to remove guests (crew),
 * lose a number of days, and gain credits. It tracks whether the effect has been applied
 * to prevent repeated application.
 */
public final class SldAbandonedShip extends SldAdvCard {
    private final int daysLost;
    private final int credits;
    private final int crewLost;

    private boolean effectTaken = false;

    /**
     * Constructs a new Abandoned Ship card with the specified parameters.
     *
     * @param id the unique card ID
     * @param level the card's difficulty level
     * @param daysLost the number of days lost when the effect is applied
     * @param credits the amount of credits gained when the effect is applied
     * @param crewLost the number of crew members required to activate the card
     */
    public SldAbandonedShip(int id, int level, int daysLost, int credits, int crewLost) {
        super(id, level);
        this.daysLost = daysLost;
        this.credits = credits;
        this.crewLost = crewLost;
    }

    /**
     * Returns the name of this card.
     *
     * @return the string "Abandoned Ship"
     */
    public String getCardName() {
        return "Abandoned Ship";
    }

    /**
     * Retrieves the number of crew members lost due to this event.
     *
     * @return The number of crew members lost.
     */
    @Override
    public int getCrewLost() {
        return crewLost;
    }

    /**
     * Retrieves the number of days lost due to this event.
     *
     * @return The number of days lost.
     */
    @Override
    public int getDaysLost() {
        return daysLost;
    }

    /**
     * Retrieves the number of credits gained or lost due to this event.
     *
     * @return The number of credits.
     */
    @Override
    public int getCredits() {return credits;}

    /**
     * Loads an {@code SldAbandonedShip} object from a JSON node.
     * This static method parses the provided {@link JsonNode} to extract the necessary
     * attributes (id, level, daysLost, credits, and crewLost) and
     * constructs a new {@code SldAbandonedShip} instance.
     *
     * @param node The {@link JsonNode} containing the data for the abandoned ship.
     * @return A new {@code SldAbandonedShip} instance populated with data from the JSON node.
     */
    public static SldAbandonedShip loadAbandonedShip(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        int credits = node.path("credits").asInt();
        int crewLost = node.path("crewLost").asInt();

        return new SldAbandonedShip(id, level, daysLost, credits, crewLost);
    }

    /**
     * Initializes the card with game context, filtering which players may interact with it.
     * <p>
     * Only players with enough crew are considered eligible to activate the card.
     *
     * @param game the game instance
     */
    public void init(GameServer game) {
        FlyBoard board = game.getFlyboard();

        this.game = game;
        this.flyBoard = game.getFlyboard();

        this.allowedPlayers = board.getScoreBoard().stream()
                .filter(player -> player.getShipBoard().getQuantityGuests() >= this.crewLost)
                .toList();

        playerIterator = allowedPlayers.iterator();
        effectTaken = false;
    }

    /**
     * Applies the card's effect to the current player, if they choose to activate it.
     * <p>
     * The player will lose crew, gain credits, and lose days. Events are generated
     * for each removed crew component. This method can only be called once per player.
     *
     * @param nickname the nickname of the player attempting activation
     * @param wantsToActivate true if the player agrees to apply the effect
     * @param housingCordinatesList the coordinates of the crew components to be removed
     * @throws IncorrectFlyBoardException if activation conditions are not met
     * @throws BadPlayerException if an unauthorized player attempts to apply the effect
     */
    public void applyEffect(String nickname, boolean wantsToActivate, List<Cordinate> housingCordinatesList) {
        if (! nickname.equals(actualPlayer.getNickname())) {
            throw new IncorrectFlyBoardException("Not " + nickname + " turn to play");
        }

        this.state = CardState.APPLYING;
        Player player = flyBoard.getPlayerByUsername(nickname);

        if (player.equals(actualPlayer)) {
            if (wantsToActivate) {
                if (housingCordinatesList == null) {
                    throw new IncorrectFlyBoardException("List is null");
                }
                if (housingCordinatesList.isEmpty()) {
                    throw new IncorrectFlyBoardException("List is empty");
                }
                if (housingCordinatesList.size() != this.crewLost) {
                    throw new IncorrectFlyBoardException("List has wrong size");
                }

                for (Cordinate cord : housingCordinatesList) {
                    flyBoard.getPlayerByUsername(actualPlayer.getNickname()).getShipBoard().getOptComponentByCord(cord).get().removeGuest();
                }
                flyBoard.moveDays(flyBoard.getPlayerByUsername(actualPlayer.getNickname()), -this.daysLost);
                flyBoard.getPlayerByUsername(actualPlayer.getNickname()).addCredits(this.credits);

                synchronized (game.getEventsQueue()){
                    for (Cordinate cord : housingCordinatesList){
                        int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();

                        Event event = new RemoveGuestEvent(null, idComp);
                        game.addEvent(event);
                    }

                    while (! game.getEventsQueue().isEmpty()) {
                        try {
                            game.getEventsQueue().wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                effectTaken = true;
                setNextPlayer();

            } else {
                return;
            }
        } else {
            throw new BadPlayerException(this.getCardName());
        }
    }


    /**
     * Advances to the next player, if any, or finalizes the card state if the effect has been applied.
     */
    @Override
    public void setNextPlayer() {
        if (playerIterator.hasNext() && !effectTaken) {
            this.actualPlayer = this.playerIterator.next();
            setState(CardState.ACCEPTATION_CHOICE);
        } else {
            setState(CardState.FINALIZED);
        }
    }
}
