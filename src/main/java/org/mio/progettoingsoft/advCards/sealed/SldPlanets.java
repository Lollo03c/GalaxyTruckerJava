package org.mio.progettoingsoft.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.Planet;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.BadPlayerException;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.LandOnPlanetEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;

/**
 * Represents a "Planets" adventure card in the game.
 * This card allows players to land on specific planets, potentially incurring
 * penalties or gaining benefits. It manages the landing process, tracks landed players,
 * and applies effects based on planet interactions.
 * It extends {@link SldAdvCard} and provides specific logic for planet exploration.
 */
public final class SldPlanets extends SldAdvCard {
    private final int daysLost;
    private final List<Planet> planets;
    private Set<Player> finishedGoodsPlacement = new HashSet<>();
    private boolean readyToProceed = false;

    private Iterator<Planet> planetIterator;
    Planet actualPlanet;
    private final Map<Planet, Player> landedPlayers;
    private int passedPlayers;

    /**
     * Constructs a new {@code SldPlanets} adventure card.
     *
     * @param id The unique identifier for this planets card.
     * @param level The level of the planets, potentially influencing their effects.
     * @param daysLost The number of days lost as a penalty for landing on a planet.
     * @param planets A list of {@link Planet} objects available on this card.
     */
    public SldPlanets(int id, int level, int daysLost, List<Planet> planets) {
        super(id, level);
        this.daysLost = daysLost;
        this.planets = planets;
        this.landedPlayers = new HashMap<>();
        this.passedPlayers = 0;
    }

    /**
     * Retrieves a map of planets to the players who have landed on them.
     *
     * @return A {@link Map} where keys are {@link Planet} objects and values are the {@link Player} objects who landed on them.
     */
    public Map<Planet, Player> getLandedPlayers() {
        return landedPlayers;
    }

    /**
     * Overrides the base method to return the list of planets associated with this card.
     *
     * @return A list of {@link Planet} objects available on this card.
     */
    @Override
    public List<Planet> getPlanets(){
        return planets;
    }

    /**
     * Overrides the base method to return the number of days lost associated with this card.
     *
     * @return The number of days lost.
     */
    @Override
    public int getDaysLost(){return daysLost;}

    /**
     * Overrides the base method to return the name of this card.
     *
     * @return The string "Planets".
     */
    @Override
    public String getCardName() {
        return "Planets";
    }

    /**
     * Overrides the base method to return the count of players who have passed their turn
     * or made a decision (landed or skipped landing) on this card.
     *
     * @return The number of players who have passed.
     */
    @Override
    public int getPassedPlayers(){
        return passedPlayers;
    }

    /**
     * Static factory method to load an {@code SldPlanets} object from a JSON node.
     * It parses the node to extract ID, level, days lost, and a list of planets,
     * then constructs a new {@code SldPlanets} instance.
     *
     * @param node The {@link JsonNode} containing the Planets card data.
     * @return A new {@code SldPlanets} instance populated with data from the JSON node.
     */
    public static SldPlanets loadPlanets(JsonNode node){
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        int daysLost = node.path("daysLost").asInt();
        List<Planet> planets = new ArrayList<>();
        JsonNode planetsNode = node.path("planets");
        for(JsonNode planet : planetsNode) {
            planets.add(Planet.stringToPlanet(planet));
        }

        return new SldPlanets(id, level, daysLost, planets);
    }

    /**
     * Initializes the Planets card's state and context within the game.
     * Resets the {@code passedPlayers} count, sets up game and fly board references,
     * and initializes iterators for players and planets.
     *
     * @param game The {@link GameServer} instance managing the current game.
     */
    @Override
    public void init(GameServer game) {
        passedPlayers = 0;

        this.game = game;
        this.flyBoard = game.getFlyboard();
        this.allowedPlayers = new ArrayList<>(flyBoard.getScoreBoard());
        this.playerIterator = allowedPlayers.iterator();
        this.planetIterator = planets.iterator();
    }

    /**
     * Handles a player's decision to land on a planet or skip landing.
     * This method is called when a player makes a choice regarding landing.
     *
     * @param player The {@link Player} making the landing decision.
     * @param planetIndex The index of the planet the player wishes to land on. Use -1 if the player does not want to land.
     * @throws IllegalStateException if the card is not in the {@code PLANET_CHOICE} state.
     * @throws BadPlayerException if the provided player is not the current active player.
     * @throws BadParameterException if the planet index is out of bounds or the chosen planet is already taken.
     */
    @Override
    // if the planetIndex parameter is -1, the player doesn't want to land
    public void land(Player player, int planetIndex) {
        if (this.state != CardState.PLANET_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (this.actualPlayer.equals(player)) {
            passedPlayers++;
            Logger.debug("numero giocatori passati "   + passedPlayers);
            if (planetIndex == -1) {
                //setNextPlayer();
            } else {
                if (planetIndex >= this.planets.size() || planetIndex < 0) {
                    throw new BadParameterException("Index out of list bounds");
                }
                if (this.planets.get(planetIndex).getPlayer().isPresent()) {
                    throw new BadParameterException("This planet is already taken");
                }
                this.planets.get(planetIndex).land(actualPlayer);
                landedPlayers.put(planets.get(planetIndex), actualPlayer);
//                boolean allTaken = true;
//                for (Planet planet : this.planets) {
//                    if (planet.getPlayer().isEmpty()) {
//                        allTaken = false;
//                        break;
//                    }
//                }
//                if (allTaken) {
//                    applyEffect(board);
//                } else {
//                    nextPlayer(board);
//                }
            }
//            if( passedPlayers == game.getNumPlayers() || landedPlayers.size() == planets.size() ) {
//                Logger.debug("numero giocatori passati "   + passedPlayers);
//                applyEffect();
//            }else {
//                setNextPlayer();
//            }
        } else {
            throw new BadPlayerException("The player " + actualPlayer.getNickname() + " cannot play " + this.getCardName() + " at the moment");
        }
    }

    /**
     * Applies the effect of the Planets card.
     * For each player who successfully landed on a planet, they incur a penalty of {@code daysLost}.
     * After applying the effects, it proceeds to process the next planet.
     */
    public void applyEffect() {
        Logger.debug("applyEffect() called with landedPlayers: " + landedPlayers);
        for (Player player : landedPlayers.values()){
            flyBoard.moveDays(player, -daysLost);
        }

        setNextPlanet();
    }

    /**
     * Sets the next player to make a landing choice.
     * If there are more players in the {@code allowedPlayers} list, the card state
     * transitions to {@code PLANET_CHOICE} for the next player.
     * The logic for finalizing the card or moving to {@code applyEffect} after all players have chosen
     * is expected to be handled by the calling context or external game loop based on {@code passedPlayers} count.
     */
    @Override
    public void setNextPlayer() {
        if (playerIterator.hasNext() ) {
            actualPlayer = playerIterator.next();
            setState(CardState.PLANET_CHOICE);
        } else {
//            Logger.debug("entro in FINALIZED : " + playerIterator+ " "+ actualPlayer);
//            setState(CardState.FINALIZED);
        }
    }

    /**
     * Iterates through the planets on the card, processing each one.
     * If a planet has a player landed on it, that player becomes the {@code actualPlayer},
     * and a {@link LandOnPlanetEvent} is added to the game events.
     * Once all planets have been iterated, the card state transitions to {@code FINALIZED}.
     */
    public void setNextPlanet(){
        if (planetIterator.hasNext()){
            actualPlanet = planetIterator.next();
            if (! landedPlayers.containsKey(actualPlanet)){
                setNextPlanet();
                return;
            }

            actualPlayer = landedPlayers.get(actualPlanet);
            Event event = new LandOnPlanetEvent(landedPlayers.get(actualPlanet).getNickname(), actualPlanet);
            game.addEvent(event);
        }
        else{
            setState(CardState.FINALIZED);
        }
    }
}