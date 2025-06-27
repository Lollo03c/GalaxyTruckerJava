package org.mio.progettoingsoft.model.advCards.sealed;

import com.fasterxml.jackson.databind.JsonNode;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.model.Cordinate;
import org.mio.progettoingsoft.model.advCards.*;
import org.mio.progettoingsoft.model.enums.Direction;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.RemoveComponentEvent;
import org.mio.progettoingsoft.model.events.RemoveGuestEvent;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.utils.Logger;

import java.util.*;

/**
 * Represents a "Combat Zone" adventure card in the game.
 * This card type involves various combat-related interactions,
 * including engine power checks, firing drills, and cannon penalties,
 * leading to potential crew loss or component destruction.
 * It extends {@link SldAdvCard} and provides specific implementations
 * for its combat-related effects and data handling.
 */
public final class SldCombatZone extends SldAdvCard {
    private final List<CombatLine> lines;
    private int actualLineIndex;
    private Penalty tempPenalty;

    private Iterator<Penalty> penaltyIterator;
    private Iterator<CombatLine> lineIterator;
    private CombatLine actualLine;

    private List<Player> askEngine = new ArrayList<>();
    private Iterator<Player> askEngineIterator;
    private Map<Player, Integer> enginePower = new HashMap<>();

    private List<Player> askFire = new ArrayList<>();
    private Iterator<Player> askFireIterator;
    private Map<Player, Double> firePower = new HashMap<>();

    private Iterator<CannonPenalty> cannonIterator = getCannonPenalty().iterator();
    private CannonPenalty actualCannon;


    /**
     * Constructs a new SldCombatZone card.
     *
     * @param id The unique identifier for the card.
     * @param level The difficulty level of the card.
     * @param lines A list of {@link CombatLine} defining the combat scenarios and outcomes.
     */
    public SldCombatZone(int id, int level, List<CombatLine> lines) {
        super(id, level);
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Retrieves the reported fire power of players during a combat phase.
     *
     * @return A map where keys are {@link Player} objects and values are their reported fire power.
     */
    public Map<Player, Double> getFirePower() {
        return firePower;
    }

    /**
     * Retrieves the reported engine power of players during a combat phase.
     *
     * @return A map where keys are {@link Player} objects and values are their reported engine power.
     */
    public Map<Player, Integer> getEnginePower() {
        return enginePower;
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
     * Overrides the base method to return the specific combat lines for this card.
     *
     * @return A list of {@link CombatLine} objects associated with this Combat Zone.
     */
    @Override
    public List<CombatLine> getLines() {
        return lines;
    }

    /**
     * Overrides the base method to return the name of this card.
     *
     * @return The string "Combat Zone".
     */
    @Override
    public String getCardName() {
        return "Combat Zone";
    }

    /**
     * Overrides the base method to return the number of crew lost specific to certain Combat Zone cards.
     * For card ID 16, 2 crew members are lost. Otherwise, no crew are lost by default.
     *
     * @return The number of crew members lost.
     */
    @Override
    public int getCrewLost() {
        if (getId() == 16) {
            return 2;
        }

        return 0;
    }

    /**
     * Overrides the base method to return the specific cannon penalties for certain Combat Zone cards.
     * Returns predefined penalties for card IDs 16 and 35.
     *
     * @return A list of {@link CannonPenalty} objects.
     */
    @Override
    public List<CannonPenalty> getCannonPenalty() {
        if (getId() == 16) {
            return new ArrayList<>(List.of(
                    new CannonPenalty(Direction.BACK, CannonType.LIGHT),
                    new CannonPenalty(Direction.BACK, CannonType.HEAVY)
            ));
        }
        else if (getId() == 35){
            return new ArrayList<>(List.of(
                    new CannonPenalty(Direction.FRONT, CannonType.LIGHT),
                    new CannonPenalty(Direction.RIGHT, CannonType.LIGHT),
                    new CannonPenalty(Direction.LEFT, CannonType.LIGHT),
                    new CannonPenalty(Direction.BACK, CannonType.HEAVY)
            ));
        }

        return Collections.emptyList();
    }

    /**
     * Static factory method to load an {@code SldCombatZone} object from a JSON node.
     * It parses the node to extract ID, level, and combat lines,
     * including complex penalty structures like cannon penalties.
     *
     * @param node The {@link JsonNode} containing the Combat Zone card data.
     * @return A new {@code SldCombatZone} instance populated with data from the JSON node.
     */
    public static SldCombatZone loadCombatZone(JsonNode node) {
        int id = node.path("id").asInt();
        int level = node.path("level").asInt();
        List<CombatLine> combatLines = new ArrayList<>();
        List<Penalty> cannonPenalties = new ArrayList<>();
        JsonNode criterionsNode = node.path("criterion");
        JsonNode penaltyNode = node.path("penalty");
        for (int j = 0; j < criterionsNode.size(); j++) {
            if (penaltyNode.get(j).get(0).asText().equals("cannonsPenalty")) {
                for (JsonNode cannonsPenalty : penaltyNode.get(j).get(1)) {
                    cannonPenalties.add(CannonPenalty.stringToCannonPenalty(cannonsPenalty.get(1).asText(), cannonsPenalty.get(0).asText()));
                }
                combatLines.add(new CombatLine(Criterion.stringToCriterion(criterionsNode.get(j).asText()), cannonPenalties));
            } else {
                List<Penalty> penaltyList = new ArrayList<>();
                penaltyList.add(LoseSomethingPenalty.stringToPenalty(penaltyNode.get(j).get(0).asText(), penaltyNode.get(j).get(1).asInt()));
                combatLines.add(new CombatLine(Criterion.stringToCriterion(criterionsNode.get(j).asText()), penaltyList));
            }
        }

        return new SldCombatZone(id, level, combatLines);
    }

    /**
     * Initializes the Combat Zone card's state and context within the game.
     * Sets up the game, fly board, allowed players, and initializes iterators for player
     * interactions based on the specific card ID (e.g., ID 16 for crew loss, ID 36 for fire drills).
     *
     * @param game The {@link GameServer} instance managing the current game.
     */
    @Override
    public void init(GameServer game) {

        this.game = game;
        flyBoard = game.getFlyboard();

        this.allowedPlayers = flyBoard.getScoreBoard();


        askFire = new ArrayList<>(flyBoard.getScoreBoard());
        askFireIterator = askFire.iterator();

        if (getId() == 16) {
            Player minCrew = null;
            for (Player player : flyBoard.getScoreBoard()) {
                if (minCrew == null || player.getShipBoard().getQuantityGuests() < minCrew.getShipBoard().getQuantityGuests()) {
                    minCrew = player;
                }
            }
            flyBoard.moveDays(minCrew, -3);

            askEngine = new ArrayList<>(flyBoard.getScoreBoard());
            askEngineIterator = askEngine.iterator();
            setNextPlayerEngine();

        }
        else if (getId() == 36){
            askFire = new ArrayList<>(flyBoard.getScoreBoard());
            askFireIterator = askFire.iterator();

            setNextPlayerFire();
        }
    }

    /**
     * Sets the next player for the engine power choice phase or proceeds to calculate
     * the lowest engine power and applies effects based on the card ID.
     * This method transitions the card state to {@code ENGINE_CHOICE} or subsequent states.
     */
    public void setNextPlayerEngine() {
        if (askEngineIterator.hasNext()) {
            actualPlayer = askEngineIterator.next();
            setState(CardState.ENGINE_CHOICE);
        } else {

            Player minPlayer = null;
            int minPower = 0;

            for (Player player : enginePower.keySet()) {
                int power = 2 * enginePower.get(player) + player.getShipBoard().getBaseEnginePower();

                if (minPlayer == null || power < minPower) {
                    minPlayer = player;
                    minPower = power;
                }
                Logger.info(player.getNickname() + " " + power);
            }

            if (getId() == 16) {
                actualPlayer = minPlayer;

                setState(CardState.CREW_REMOVE_CHOICE);
            } else if (getId() == 36) {
                minPlayer.getShipBoard().stoleGood(3);

                Player minCrew = null;
                for (Player player : flyBoard.getScoreBoard()) {
                    if (minCrew == null || player.getShipBoard().getQuantityGuests() < minCrew.getShipBoard().getQuantityGuests()) {
                        minCrew = player;
                    }
                }
                actualPlayer = minPlayer;
                setNextCannon();
            }
        }
    }

    /**
     * Sets the next player for the fire drill choice phase or proceeds to calculate
     * the lowest fire power and applies effects based on the card ID.
     * This method transitions the card state to {@code DRILL_CHOICE} or subsequent states.
     */
    public void setNextPlayerFire() {
        if (askFireIterator.hasNext()) {
            actualPlayer = askFireIterator.next();
            setState(CardState.DRILL_CHOICE);
        } else {
            Player minPlayer = null;

            for (Player player : firePower.keySet()) {

                if (minPlayer == null || firePower.get(player) < firePower.get(minPlayer)) {
                    minPlayer = player;
                }
                Logger.info(player.getNickname() + " " + firePower.get(player));
            }

            if (getId() == 16) {
                actualPlayer = minPlayer;
                cannonIterator = getCannonPenalty().iterator();
                setNextCannon();
            } else if (getId() == 36) {
                flyBoard.moveDays(minPlayer, -4);

                askEngine = new ArrayList<>(flyBoard.getScoreBoard());
                askEngineIterator = askEngine.iterator();

                setNextPlayerEngine();
            }
        }
    }

    /**
     * Sets the next cannon penalty to be applied or finalizes the card's effect if no more penalties.
     * This method transitions the card state to {@code DICE_ROLL} or {@code FINALIZED}.
     */
    public void setNextCannon() {
        if (cannonIterator.hasNext()) {
            actualCannon = cannonIterator.next();
            setState(CardState.DICE_ROLL);
        } else {
            setState(CardState.FINALIZED);
        }
    }

    /**
     * Applies the result of a cannon penalty, potentially removing energy or destroying a ship component.
     * After applying the penalty, it proceeds to the next cannon penalty.
     *
     * @param nickname The nickname of the player affected by the penalty.
     * @param destroyed A boolean indicating if a component was destroyed.
     * @param energy A boolean indicating if energy was removed.
     */
    public void setNextCannon(String nickname, boolean destroyed, boolean energy) {

        Player player = flyBoard.getPlayerByUsername(nickname);

        if (energy)
            player.getShipBoard().removeEnergy(1);

        if (destroyed) {
            Optional<Cordinate> optCord = actualCannon.findHit(player.getShipBoard(), actualCannon.getNumber());
            player.getShipBoard().removeComponent(optCord.get());

            Event event = new RemoveComponentEvent(nickname, optCord.get());
            game.addEvent(event);
        }


        setNextCannon();
    }

    /**
     * Stores the engine power chosen by a specific player.
     *
     * @param player The {@link Player} who made the choice.
     * @param power The integer value of the engine power chosen.
     */
    public void setEnginePower(Player player, int power) {
        enginePower.put(player, power);
    }

    /**
     * Calculates and stores the fire power for a player based on their ship's drills and base power.
     * After calculating, it proceeds to the next player for fire drill choice.
     *
     * @param player The {@link Player} who performed the drill.
     * @param drillsCord A list of {@link Cordinate} representing the drills used.
     */
    public void setDrills(Player player, List<Cordinate> drillsCord){
        double power = player.getShipBoard().getBaseFirePower();
        for (Cordinate cord : drillsCord) {
            power += player.getShipBoard().getOptComponentByCord(cord).get().getFirePower(true);
        }
        firePower.put(player, power);
        setNextPlayerFire();

    }

    /**
     * Handles the removal of crew members from a player's ship.
     * This method is typically called when a player needs to remove crew due to a penalty.
     * It updates the ship's state and logs the event.
     *
     * @param nickname The nickname of the player from whom crew is to be removed.
     * @param cordinates A list of {@link Cordinate} representing the components from which crew is removed.
     * @throws IncorrectFlyBoardException if the provided nickname does not match the actual player.
     */
    public void removeCrew(String nickname, List<Cordinate> cordinates) {
        if (!nickname.equals(actualPlayer.getNickname())) {
            throw new IncorrectFlyBoardException("");
        }

        for (Cordinate cord : cordinates) {
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();
            flyBoard.getComponentById(idComp).removeGuest();

        }

        for (Cordinate cord : cordinates) {
            int idComp = actualPlayer.getShipBoard().getOptComponentByCord(cord).get().getId();

            Event event = new RemoveGuestEvent(nickname, idComp);
            game.addEvent(event);
        }

        if (getId() == 16){
            askFire = new ArrayList<>(flyBoard.getScoreBoard());
            askFireIterator = askFire.iterator();

            setNextPlayerFire();
        }
    }
}
