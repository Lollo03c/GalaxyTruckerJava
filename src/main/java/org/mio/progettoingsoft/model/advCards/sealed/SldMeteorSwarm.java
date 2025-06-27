package org.mio.progettoingsoft.model.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.model.advCards.Meteor;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the sealed advanced card "Meteor Swarm", which simulates a sequence of meteors
 * threatening the players' ships.
 * <p>
 * Players must react to each incoming meteor by choosing to use energy to defend or let the meteor hit.
 * Each meteor affects multiple players, and the card processes them sequentially.
 */
public final class SldMeteorSwarm extends SldAdvCard{
    private final List<Meteor> meteors;
    private Iterator<Meteor> meteorIterator;
    private Meteor actualMeteor;

    /**
     * Constructs a new Meteor Swarm card.
     *
     * @param id the unique identifier of the card
     * @param level the difficulty level of the card
     * @param meteors the list of meteors included in this event
     */
    public SldMeteorSwarm(int id, int level, List<Meteor> meteors) {
        super(id, level);
        this.meteors = meteors;
    }

    /**
     * Returns the display name of the card.
     *
     * @return the string "Meteor Swarm"
     */
    @Override
    public String getCardName() {
        return "Meteor Swarm";
    }

    /**
     * Retrieves the list of meteors associated with this card.
     *
     * @return A list of {@link Meteor} objects representing the meteors.
     */
    @Override
    public List<Meteor> getMeteors() {
        return meteors;
    }

    /**
     * Deserializes a {@code SldMeteorSwarm} card from a JSON node.
     *
     * @param node the JSON node containing the card's data
     * @return a new {@code SldMeteorSwarm} instance
     */
    public static SldMeteorSwarm loadMeteorSwarm(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        List<Meteor> meteors = new ArrayList<>();
        JsonNode meteorsNode = node.path("meteors");
        for(JsonNode meteor : meteorsNode) {
            meteors.add(Meteor.stringToMeteor(meteor.get(1).asText(),meteor.get(0).asText()));
        }

        return new SldMeteorSwarm(id, level, meteors);
    }

    /**
     * Initializes the card and sets up player and meteor iteration.
     * The first meteor will be processed once {@link #setNextMeteor()} is called.
     *
     * @param game the current game instance
     */
    @Override
    public void init(GameServer game) {
        this.game  = game;
        this.flyBoard = game.getFlyboard();

        allowedPlayers = new ArrayList<>(flyBoard.getScoreBoard());
        playerIterator = allowedPlayers.iterator();
        actualPlayer = playerIterator.next();

        meteorIterator = meteors.iterator();
    }

    /**
     * Advances to the next meteor in the sequence.
     * Sets the card state to {@code DICE_ROLL} if there is another meteor,
     * or {@code FINALIZED} if all meteors have been processed.
     */
    public synchronized void setNextMeteor(){
        if (meteorIterator.hasNext()){
            actualMeteor = meteorIterator.next();
            setState(CardState.DICE_ROLL);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }

    public Meteor getActualMeteor(){
        return actualMeteor;
    }

    /**
     * Resolves a player's interaction with the current meteor.
     * <p>
     * The player can choose whether to destroy the meteor and whether to consume energy to do so.
     * If all affected players have responded, the card progresses to the next meteor.
     *
     * @param nick the nickname of the responding player
     * @param destroyed true if the player destroyed the meteor
     * @param energy true if energy was consumed to block the meteor
     * @throws IncorrectFlyBoardException if the player has already responded or is invalid
     */
    public synchronized void setNextMeteor(String nick, boolean destroyed,  boolean energy){
        List<String> nicknames = flyBoard.getPlayers().stream().map(p -> p.getNickname()).toList();
        if (!nicknames.contains(nick)){
            Logger.error("eccezion");
            throw new IncorrectFlyBoardException("no player found");
        }


        if (actualMeteor.getPlayerResponses().contains(nick))
            throw new IncorrectFlyBoardException("player has already answered");

        if (energy){
            flyBoard.getPlayerByUsername(nick).getShipBoard().removeEnergy(1);
        }

        if (destroyed){
            actualMeteor.destroy(flyBoard.getPlayerByUsername(nick), game);
        }

        actualMeteor.getNickHit().remove(nick);
        if (actualMeteor.getNickHit().isEmpty())
            setNextMeteor();

    }
}
