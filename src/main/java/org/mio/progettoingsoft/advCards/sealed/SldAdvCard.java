package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.CombatLine;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.advCards.Planet;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;
import org.mio.progettoingsoft.views.tui.VisualCard;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents an abstract sealed class for adventure cards in the game.
 * This class provides common properties and behaviors for all adventure card types,
 * while allowing specific implementations in its permitted subclasses.
 * It enforces that subclasses must implement {@link #getCardName()} and {@link #init(GameServer)}.
 *
 * <p>Many methods throw {@link RuntimeException} by default, indicating that a specific card type
 * does not support that particular action or retrieve that specific information. Subclasses
 * should override these methods if the functionality is applicable to them.</p>
 */
public abstract sealed class SldAdvCard permits SldAbandonedShip, SldEpidemic, SldOpenSpace, SldSlavers, SldAbandonedStation, SldCombatZone, SldStardust, SldSmugglers, SldPlanets, SldPirates, SldMeteorSwarm {
    private final int level;
    private final int id;
    protected List<Player> allowedPlayers;
    protected Player actualPlayer;
    protected Iterator<Player> playerIterator;
    protected CardState state;

    protected GameServer game;
    protected FlyBoard flyBoard;


    /**
     * Constructs a new SldAdvCard with the specified ID and level.
     *
     * @param id The unique identifier for the card.
     * @param level The difficulty level of the card.
     */
    public SldAdvCard(int id, int level) {
        this.id = id;
        this.level = level;
        this.state = CardState.IDLE;
    }

    /**
     * Abstract method to retrieve the specific name of the adventure card.
     * Each concrete card type must provide its own name.
     *
     * @return The name of the card as a String.
     */
    public abstract String getCardName();

    /**
     * Retrieves the current state of the card, indicating its phase in the game.
     *
     * @return The current {@link CardState}.
     */
    public CardState getState() {
        return state;
    }

    /**
     * Retrieves the player currently interacting with this card.
     *
     * @return The {@link Player} currently active on this card.
     */
    public Player getActualPlayer() {
        return actualPlayer;
    }

    /**
     * Retrieves the unique identifier of the card.
     *
     * @return The card's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the level of the card, often indicating its difficulty or impact.
     *
     * @return The card's level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Throws an exception indicating that this card type does not involve landed players.
     * Subclasses that manage landed players should override this method.
     *
     * @return A map of planets to players that have landed, if applicable.
     * @throws RuntimeException if this card does not have any landed players.
     */
    public Map<Planet, Player> getLandedPlayers() {
        throw new RuntimeException("this card does not have any landed players");
    }

    /**
     * Throws an exception indicating that this card type does not involve combat lines.
     * Subclasses that implement combat mechanics should override this method.
     *
     * @return A list of {@link CombatLine} if this card involves combat.
     * @throws RuntimeException if this card doesn't have combat lines.
     */
    public List<CombatLine> getLines() {
        throw new RuntimeException("this card doesn't have combat lines");
    }

    /**
     * Throws an exception indicating that this card type does not track passed players.
     * Subclasses that track passed players should override this method.
     *
     * @return The number of players who have passed.
     * @throws RuntimeException if this card does not have passed players.
     */
    public int getPassedPlayers() {
        throw new RuntimeException("this card does not have passed players");
    }

    /**
     * Throws an exception indicating that this card type does not require crew.
     * Subclasses that require crew should override this method.
     *
     * @return The number of crew members needed.
     * @throws RuntimeException if this card doesn't need crew.
     */
    public int getCrewNeeded() throws RuntimeException {
        throw new RuntimeException("this card doesn't need crew");
    }

    /**
     * Throws an exception indicating that this card type does not involve meteors.
     * Subclasses related to meteor events should override this method.
     *
     * @return A list of {@link Meteor} objects.
     * @throws RuntimeException if this card doesn't have meteors.
     */
    public List<Meteor> getMeteors() {
        throw new RuntimeException("this card doesn't have meteors");
    }

    /**
     * Throws an exception indicating that this card type does not involve cannon penalties.
     * Subclasses related to combat or penalties should override this method.
     *
     * @return A list of {@link CannonPenalty} objects.
     * @throws RuntimeException if this card doesn't have cannon penalties.
     */

    public List<CannonPenalty> getCannonPenalty() {
        throw new RuntimeException("this card doesn't have cannon penalties");
    }

    /**
     * Throws an exception indicating that this card type does not involve stolen goods.
     * Subclasses related to theft or trading should override this method.
     *
     * @return The amount of goods stolen.
     * @throws RuntimeException if this card doesn't have stolen goods.
     */
    public int getStolenGoods() {
        throw new RuntimeException("this card doesn't have stolen goods");
    }

    /**
     * Retrieves a list of goods associated with this card.
     * By default, returns an empty list, as many cards do not involve goods.
     * Subclasses that involve goods should override this method.
     *
     * @return A list of {@link GoodType} objects.
     */
    public List<GoodType> getGoods() {
        return Collections.emptyList();
    }

    /**
     * Throws an exception indicating that this card type does not result in crew loss.
     * Subclasses that can cause crew loss should override this method.
     *
     * @return The number of crew members lost.
     * @throws RuntimeException if this card doesn't have crew lost.
     */
    public int getCrewLost() {
        throw new RuntimeException("this card doesn't have crew lost");
    }

    /**
     * Throws an exception indicating that this card type does not involve credits.
     * Subclasses that affect credits (gain or loss) should override this method.
     *
     * @return The amount of credits.
     * @throws RuntimeException if this card doesn't have credits.
     */
    public int getCredits() {
        throw new RuntimeException("this card doesn't have credits");
    }

    /**
     * Throws an exception indicating that this card type does not result in lost days.
     * Subclasses that cause time loss should override this method.
     *
     * @return The number of days lost.
     * @throws RuntimeException if this card doesn't have days lost.
     */
    public int getDaysLost() {
        throw new RuntimeException("this card doesn't have days lost");
    }

    /**
     * Throws an exception indicating that this card type does not have a strength attribute.
     * Subclasses involved in combat or challenges should override this method.
     *
     * @return The strength value of the card.
     * @throws RuntimeException if this card doesn't have strength.
     */
    public int getStrength() {
        throw new RuntimeException("this card doesn't have strength");
    }

    /**
     * Throws an exception indicating that this card type does not involve planets.
     * Subclasses related to planet exploration or interaction should override this method.
     *
     * @return A list of {@link Planet} objects.
     * @throws RuntimeException if this card doesn't have planets.
     */
    public List<Planet> getPlanets() {
        throw new RuntimeException("this card doesn't have planets");
    }

    /**
     * Abstract method to initialize the card's state and context within the game.
     * This method must be called immediately after drawing the card to set the game state
     * to {@code CARD_EFFECT} and prepare the card for interaction.
     *
     * @param game The {@link GameServer} instance managing the current game.
     */
    public abstract void init(GameServer game);

    /**
     * Applies the specific effect of the adventure card.
     * Throws a {@link RuntimeException} if this method is called on a card
     * that does not have a defined effect or if there's an issue with its implementation.
     * Subclasses must override this method to provide their unique effects.
     */
    public void applyEffect() {
        throw new RuntimeException("problem with the apply effect method");
    }

    /**
     * Throws an exception indicating that this card type does not support player landing on planets.
     * Subclasses that allow players to land on planets should override this method.
     *
     * @param player The {@link Player} attempting to land.
     * @param planetIndex The index of the planet to land on.
     * @throws RuntimeException if this card doesn't have a method to land players.
     */
    public void land(Player player, int planetIndex) {
        throw new RuntimeException("this card doesn't have method land");
    }

    /**
     * Draws the visual representation of the card to the client.
     * This method creates a {@link VisualCard} instance and triggers its drawing logic.
     */
    public void disegnaCard() {
        VisualCard visual = new VisualCard(this);
        visual.drawCard();
    }

    /**
     * Sets the new state of the card and notifies the game controller for updates.
     *
     * @param state The new {@link CardState} to set.
     */
    protected void setState(CardState state) {
        this.state = state;
        game.getController().update(this);
    }

    /**
     * Logs a debug message indicating that the "set next player" functionality is not implemented
     * for this specific card type. Subclasses that manage player turns might override this.
     */
    public void setNextPlayer() {
        Logger.debug("set next player - carta non implementata");
    }

    /**
     * Throws an exception indicating that this card type does not implement a power comparison.
     * Subclasses that involve comparing power (e.g., in combat) should override this method.
     *
     * @param board The {@link FlyBoard} context.
     * @param player The {@link Player} whose power is being compared.
     * @return The result of the power comparison.
     * @throws RuntimeException if this card doesn't implement compare Power.
     */
    public int comparePower(FlyBoard board, Player player) {
        throw new RuntimeException("this card doesn't implement compare Power");
    }
}