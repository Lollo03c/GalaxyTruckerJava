package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveComponentEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Represents a "Pirates" adventure card in the game.
 * This card type involves players confronting a pirate threat,
 * requiring them to match or exceed a certain combat strength using their ship's drills.
 * Failing to do so results in penalties like losing days or taking cannon damage.
 * It extends {@link SldAdvCard} and provides specific implementations for pirate encounters.
 */
public final class SldPirates extends SldAdvCard{
    private final int strength;
    private final int credits;
    private final List<CannonPenalty> cannons;
    private final int daysLost;

    private List<Player> penaltyPlayers = new ArrayList<>();
    private Iterator<CannonPenalty> cannonIterator;
    private CannonPenalty actualCannon;

    /**
     * Constructs a new {@code SldPirates} adventure card.
     *
     * @param id The unique identifier for this pirate card.
     * @param level The level of the pirate encounter.
     * @param daysLost The number of days lost as a penalty.
     * @param strength The combat strength of the pirates.
     * @param credits The credits rewarded for overcoming the pirates.
     * @param cannons A list of {@link CannonPenalty} objects that may be applied.
     */
    public SldPirates(int id, int level, int daysLost, int strength, int credits, List<CannonPenalty> cannons) {
        super(id, level);
        this.strength = strength;
        this.credits = credits;
        this.daysLost = daysLost;
        this.cannons = cannons;
    }

    /**
     * Retrieves the current cannon penalty being processed.
     *
     * @return The {@link CannonPenalty} currently active.
     */
    public CannonPenalty getActualCannon() {
        return actualCannon;
    }

    /**
     * Retrieves the list of players who incurred penalties during the pirate encounter.
     *
     * @return A list of {@link Player} objects who received penalties.
     */
    public List<Player> getPenaltyPlayers() {
        return penaltyPlayers;
    }

    /**
     * Overrides the base method to return the number of days lost associated with this card.
     *
     * @return The number of days lost.
     */
    @Override
    public int getDaysLost() {return daysLost;}

    /**
     * Overrides the base method to return the name of this card.
     *
     * @return The string "Pirates".
     */
    @Override
    public String getCardName() {
        return "Pirates";
    }

    /**
     * Overrides the base method to return the credits associated with this card.
     *
     * @return The number of credits.
     */
    @Override
    public int getCredits() {return credits;}

    /**
     * Overrides the base method to return the strength of the pirates on this card.
     *
     * @return The strength value.
     */
    @Override
    public int getStrength() {
        return strength;
    }

    /**
     * Overrides the base method to return the specific cannon penalties associated with this card.
     *
     * @return A list of {@link CannonPenalty} objects.
     */
    @Override
    public List<CannonPenalty> getCannonPenalty(){
        return cannons;
    }

    /**
     * Static factory method to load an {@code SldPirates} object from a JSON node.
     * It parses the node to extract ID, level, strength, days lost, cannon penalties, and reward,
     * then constructs a new {@code SldPirates} instance.
     *
     * @param node The {@link JsonNode} containing the Pirate card data.
     * @return A new {@code SldPirates} instance populated with data from the JSON node.
     */
    public static SldPirates loadPirate(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<CannonPenalty> cannons = new ArrayList<>();
        JsonNode cannonsNode = node.path("cannons");
        for (JsonNode cannon : cannonsNode) {
            cannons.add(CannonPenalty.stringToCannonPenalty(cannon.get(1).asText(),cannon.get(0).asText()));
        }
        int reward = node.path("reward").asInt();

        return new SldPirates(id, level, daysLost, strength, reward, cannons);
    }

    /**
     * Initializes the Pirates card's state and context within the game.
     * Sets up the game and fly board references, initializes the list of allowed players
     * (all players on the fly board), and prepares the iterator for cannon penalties.
     * The card state is then typically transitioned by {@code setNextPlayer()}.
     *
     * @param game The {@link GameServer} instance managing the current game.
     */
    @Override
    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();

       allowedPlayers = new ArrayList<>(flyBoard.getScoreBoard());
       playerIterator = allowedPlayers.iterator();

       cannonIterator = cannons.iterator();
    }

    /**
     * Sets the next player to confront the pirates or proceeds to apply cannon penalties.
     * If there are more players, the card state transitions to {@code DRILL_CHOICE}.
     * Otherwise, it calls {@code setNextCannon()} to apply penalties if any players failed.
     */
    @Override
    public void setNextPlayer(){
        if (playerIterator.hasNext()){
            actualPlayer = playerIterator.next();
            setState(CardState.DRILL_CHOICE);
        }
        else{
            setNextCannon();
        }
    }

    /**
     * Sets the next cannon penalty to be applied to players who failed the strength check.
     * If there are still cannon penalties to apply and players in the {@code penaltyPlayers} list,
     * it sets the card state to {@code DICE_ROLL}. Otherwise, it finalizes the card's effect.
     */
    public void setNextCannon(){
        if (cannonIterator.hasNext() && !getPenaltyPlayers().isEmpty()){
            actualPlayer = flyBoard.getScoreBoard().getFirst();

            actualCannon = cannonIterator.next();
            actualCannon.setPlayerstoHit(new ArrayList<>(getPenaltyPlayers()));
            setState(CardState.DICE_ROLL);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    /**
     * Applies the result of a cannon penalty, potentially removing energy or destroying a ship component.
     * After applying the penalty to the specified player, it removes that player from the list of
     * players to hit for the current cannon. If all players for the current cannon are processed,
     * it moves to the next cannon penalty.
     *
     * @param nickname The nickname of the player affected by the penalty.
     * @param destroyed A boolean indicating if a component was destroyed.
     * @param energy A boolean indicating if energy was removed.
     */
    public void setNextCannon(String nickname, boolean destroyed, boolean energy){

        Player player = flyBoard.getPlayerByUsername(nickname);

        if (energy)
            player.getShipBoard().removeEnergy(1);

        if (destroyed) {
            Optional<Cordinate> optCord = actualCannon.findHit(player.getShipBoard(), actualCannon.getNumber());
            player.getShipBoard().removeComponent(optCord.get());

            Event event = new RemoveComponentEvent(nickname, optCord.get());
            game.addEvent(event);
        }

        synchronized (flyBoard) {
            actualCannon.getPlayerstoHit().remove(player);
            if (actualCannon.getPlayerstoHit().isEmpty())
                setNextCannon();
        }
    }

    /**
     * Loads the fire power (drill power) provided by a player and determines the outcome of the pirate encounter.
     * Compares the player's total fire power (base + activated drills) against the pirate's strength.
     * Applies penalties or rewards based on the comparison:
     * <ul>
     * <li>If power is less than strength: player is added to {@code penaltyPlayers}.</li>
     * <li>If power is greater than strength: player loses days, gains credits, and consumes energy.</li>
     * <li>If power is equal to strength: proceeds directly to the next cannon (no direct reward/penalty here).</li>
     * </ul>
     *
     * @param player The {@link Player} whose power is being loaded.
     * @param doubleDrills A list of {@link Cordinate} representing the double drills activated by the player.
     * @throws IncorrectShipBoardException if an invalid coordinate is provided or a component is not a drill.
     */
    public void loadPower(Player player, List<Cordinate> doubleDrills){
        ShipBoard shipBoard = player.getShipBoard();
        double power = shipBoard.getBaseFirePower();

        for (Cordinate cord : doubleDrills) {
            if (shipBoard.getOptComponentByCord(cord).isEmpty())
                throw new IncorrectShipBoardException("Not valid cord");
            Component comp = shipBoard.getOptComponentByCord(cord).get();

            if (comp.getFirePower(true) <= 0)
                throw new IncorrectShipBoardException("Not a drill to activate");

            power += comp.getFirePower(true);
        }

        Logger.debug(player.getNickname() + " " + power);
        if (power < (double) this.strength){
            Logger.debug(player.getNickname() + " " + power);
            this.penaltyPlayers.add(player);
            setNextPlayer();
        }
        else if (power > this.strength){
            flyBoard.moveDays(player, -daysLost);
            player.addCredits(credits);
            player.getShipBoard().removeEnergy(doubleDrills.size());

            setNextCannon();
        }
        else{
            setNextCannon();
        }

    }
}