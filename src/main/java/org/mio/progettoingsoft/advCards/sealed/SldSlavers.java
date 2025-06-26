package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveGuestEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a "Slavers" adventure card in the game.
 * This card type involves players encountering slavers, requiring them to
 * overcome a certain strength value. Success can lead to rewards (credits and days lost, implying progress),
 * while failure results in penalties, primarily crew loss.
 * It extends {@link SldAdvCard} and provides specific logic for slaver encounters.
 */
public final class SldSlavers extends SldAdvCard {
    private final int strength;
    private final int credits;
    private final int daysLost;
    private final int crewLost;

    private List<Player> lostPlayers = new ArrayList<>();
    private Iterator<Player> loserIterator;

    /**
     * Constructs a new {@code SldSlavers} adventure card.
     *
     * @param id The unique identifier for this slaver card.
     * @param level The level of the slaver encounter.
     * @param strength The combat strength of the slavers.
     * @param daysLost The number of days lost as a reward for success.
     * @param credits The credits awarded as a reward for success.
     * @param crewLost The number of crew members lost as a penalty for failure.
     */
    public SldSlavers(int id, int level, int strength, int daysLost, int credits, int crewLost) {
        super(id, level);
        this.crewLost = crewLost;
        this.strength = strength;
        this.credits = credits;
        this.daysLost = daysLost;
    }

    /**
     * Overrides the base method to return the name of this card.
     *
     * @return The string "Slavers".
     */
    @Override
    public String getCardName() {
        return "Slavers";
    }

    /**
     * Overrides the base method to return the strength of the slavers on this card.
     *
     * @return The strength value.
     */
    @Override
    public int getStrength() {
        return strength;
    }

    /**
     * Overrides the base method to return the number of crew lost associated with this card.
     * This is the penalty for failing to overcome the slavers.
     *
     * @return The number of crew members lost.
     */
    @Override
    public int getCrewLost() {
        return crewLost;
    }

    /**
     * Overrides the base method to return the credits associated with this card.
     * This is the reward for successfully overcoming the slavers.
     *
     * @return The number of credits.
     */
    @Override
    public int getCredits() {
        return credits;
    }

    /**
     * Overrides the base method to return the number of days lost (implying progress) associated with this card.
     * This is part of the reward for successfully overcoming the slavers.
     *
     * @return The number of days lost.
     */
    @Override
    public int getDaysLost() {
        return daysLost;
    }

    /**
     * Static factory method to load an {@code SldSlavers} object from a JSON node.
     * It parses the node to extract ID, level, strength, days lost, reward (credits), and crew lost,
     * then constructs a new {@code SldSlavers} instance.
     *
     * @param node The {@link JsonNode} containing the Slaver card data.
     * @return A new {@code SldSlavers} instance populated with data from the JSON node.
     */
    public static SldSlavers loadSlaver(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int strength = node.path("strength").asInt();
        int daysLost = node.path("daysLost").asInt();
        int reward = node.path("reward").asInt();
        int crewLost = node.path("crewLost").asInt();

        return new SldSlavers(id, level, strength, daysLost, reward, crewLost);
    }

    /**
     * Initializes the Slavers card's state and context within the game.
     * Sets up game and fly board references, and initializes the list of allowed players
     * (all players on the fly board) and the player iterator.
     * The card state is then typically transitioned by {@code setNextPlayer()}.
     *
     * @param game The {@link GameServer} instance managing the current game.
     */
    public void init(GameServer game) {
        this.game = game;
        this.flyBoard = game.getFlyboard();
        this.allowedPlayers = flyBoard.getScoreBoard();
        this.playerIterator = allowedPlayers.iterator();
    }

    /**
     * Sets the next player to confront the slavers or proceeds to apply penalties to defeated players.
     * If there are more players to act, the card state transitions to {@code DRILL_CHOICE}.
     * Otherwise, it prepares to iterate through players who failed the strength check.
     */
    @Override
    public void setNextPlayer() {
        if (playerIterator.hasNext()) {
            actualPlayer = playerIterator.next();
            setState(CardState.DRILL_CHOICE);
        } else {
            loserIterator = lostPlayers.iterator();
            setNextLoser();
        }
    }


    /* !!! Card workflow after init !!! */
    /*
    - for each player, while the card is not defeated:
        - comparePlayer:
            - if >0 the card can be defeated with no extra power: the next state is directly APPLYING
            - if <=0 the card is not defeated, the player will choose if activate double drills to defeat: the next state is DRILL_CHOICE
        - applyEffect:
            - if (with or without activation) the power is > strength the card is defeated, gives rewards and skip to finish (FINALIZED)
            - if (with or without activation) the power is = strength the card is not defeated but the player has no penalties (COMPARING for next player)
            - if (with or without activation) the power is < strength the player is defeated (CREW_REMOVE_CHOICE and removeCrew method, then COMPARING for next player)
     */

    public int comparePower(FlyBoard board, Player player) {
//        if (this.state != CardState.COMPARING) {
//            throw new IllegalStateException("Illegal state: " + this.state);
//        }
//        if (actualPlayer.equals(player)) {
//            float base = player.getShipBoard().getBaseFirePower();
//            if (base > this.strength) {
//                this.state = CardState.APPLYING;
//                return 1;
//            } else if (base < this.strength) {
//                this.state = CardState.DRILL_CHOICE;
//                return -1;
//            } else {
//                this.state = CardState.DRILL_CHOICE;
//                return 0;
//            }
//        } else {
//            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
//        }
        return 0;
    }

    /**
     * Applies the effect of the Slavers card based on the player's provided drill power.
     * Players activate drills to increase their fire power and compare it against the slaver's strength.
     * Depending on the outcome, players might receive rewards (credits/days lost) or be added
     * to the list of {@code lostPlayers} for crew removal.
     *
     * @param player The {@link Player} applying the effect.
     * @param drillsCordinate A list of {@link Cordinate} representing the drills activated by the player.
     * @throws IllegalStateException if the card is not in an appropriate state (APPLYING, COMPARING, DRILL_CHOICE).
     * @throws BadPlayerException if the provided player is not the current active player.
     */
    public void applyEffect(Player player,List<Cordinate> drillsCordinate) {
        if (this.state != CardState.APPLYING && this.state != CardState.COMPARING && this.state != CardState.DRILL_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }

        if (!player.equals(this.actualPlayer)) {
            throw new BadPlayerException("The player " + player.getNickname() + " can't play " + this.getCardName() + " at the moment");
        }

        ShipBoard shipBoard = player.getShipBoard();
        shipBoard.removeEnergy(drillsCordinate.size());

        double power = player.getShipBoard().getBaseFirePower();
        Logger.debug(player.getNickname() + " " + power);
        for (Cordinate cord : drillsCordinate)
            power += shipBoard.getOptComponentByCord(cord).get().getFirePower(true);
        if (power < strength){
            lostPlayers.add(player);
            setNextPlayer();
        }
        else if (power > strength){
            actualPlayer = player;
            loserIterator = lostPlayers.iterator();
            setState(CardState.ACCEPTATION_CHOICE);
        }
        else{
            setNextPlayer();
        }
    }

    /**
     * Skips the acceptance of reward (credits and days lost) and proceeds directly
     * to apply penalties to players who failed to overcome the slavers.
     * This is typically called if the player who defeated the slavers chooses not to take the reward.
     */
    public void skipEffect(){
        loserIterator = lostPlayers.iterator();
        setNextLoser();
    }

    /**
     * Applies the reward (credits and days lost) to the player who successfully overcame the slavers.
     * After applying the reward, it proceeds to apply penalties to players who failed.
     */
    public void takeCredits(){
        flyBoard.moveDays(actualPlayer, -daysLost);
        actualPlayer.addCredits(credits);

        loserIterator = lostPlayers.iterator();
        setNextLoser();
    }

    /**
     * Sets the next player from the {@code lostPlayers} list to incur crew loss.
     * If there are more losers, the card state transitions to {@code CREW_REMOVE_CHOICE}.
     * Otherwise, the card's effect is finalized.
     */
    public void setNextLoser(){
        if (loserIterator.hasNext()){
            actualPlayer = loserIterator.next();
            setState(CardState.CREW_REMOVE_CHOICE);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    /**
     * Handles the removal of crew members from a player's ship due to failing the slaver encounter.
     * This method updates the ship's state by removing guests from specified components and logs the event.
     *
     * @param nickname The nickname of the player from whom crew is to be removed.
     * @param cordinates A list of {@link Cordinate} representing the components from which crew is removed.
     * @throws IncorrectFlyBoardException if the provided nickname does not match the actual player.
     */
    public void removeCrew(String nickname, List<Cordinate> cordinates){
        if (! nickname.equals(actualPlayer.getNickname())){
            throw new IncorrectFlyBoardException("");
        }

        for (Cordinate cord : cordinates){
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();
            flyBoard.getComponentById(idComp).removeGuest();

        }

        for (Cordinate cord : cordinates){
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();

            Event event = new RemoveGuestEvent(nickname, idComp);
            game.addEvent(event);
        }

        setNextLoser();
    }
}
