package org.mio.progettoingsoft.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.advCards.sealed.*;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.views.tui.VisualFlyboardEasy;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Concrete implementation of {@link FlyBoard} for the EASY game mode.
 */
public class FlyBoardEasy extends FlyBoard {

    /**
     * Constructs a new {@code FlyBoardEasy} instance for the EASY game mode.
     * Initializes the game board with the specified player nicknames.
     *
     * @param nicknames A {@link Set} of player nicknames participating in the game.
     */
    public FlyBoardEasy(Set<String> nicknames){
        super(GameMode.EASY, nicknames);
    }

    @Override
    public void startHourglass(int idGame) {}

    /**
     * Loads adventure cards specifically for the Easy game mode.
     * In Easy mode, only adventure cards with {@code level = 1} are loaded from
     * the "advCards.json" resource file.
     *
     * @return A {@link Map} where keys are the adventure card IDs and values are
     * the loaded {@link SldAdvCard} objects.
     */
    @Override
    protected Map<Integer, SldAdvCard> loadSldAdvCard() {
        List<SldAdvCard> loadedCards = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(new File("src/main/resources/advCards.json"));

            for (int i = 0; i < rootNode.size(); i++) {
                String type = rootNode.get(i).path("type").asText();
                int level = rootNode.get(i).path("level").asInt();

                //loads only the advCard of level 1
                if (level != 1)
                    continue;

                switch (type) {
                    case "SLAVERS":
                        loadedCards.add(SldSlavers.loadSlaver(rootNode.get(i)));
                        break;

                    case "SMUGGLERS":
                        loadedCards.add(SldSmugglers.loadSmugglers(rootNode.get(i)));
                        break;

                    case "PIRATE":
                        loadedCards.add(SldPirates.loadPirate(rootNode.get(i)));
                        break;

                    case "STARDUST":
                        loadedCards.add(SldStardust.loadStardust(rootNode.get(i)));
                        break;

                    case "EPIDEMIC":
                        loadedCards.add(SldEpidemic.loadEpidemic(rootNode.get(i)));
                        break;

                    case "OPENSPACE":
                        loadedCards.add(SldOpenSpace.loadOpenSpace(rootNode.get(i)));
                        break;

                    case "METEORSWARM":
                        loadedCards.add(SldMeteorSwarm.loadMeteorSwarm(rootNode.get(i)));
                        break;

                    case "PLANETS":
                        loadedCards.add(SldPlanets.loadPlanets(rootNode.get(i)));
                        break;

                    case "COMBATZONE":
                        loadedCards.add(SldCombatZone.loadCombatZone(rootNode.get(i)));
                        break;

                    case "ABANDONEDSHIP":
                        loadedCards.add(SldAbandonedShip.loadAbandonedShip(rootNode.get(i)));
                        break;

                    case "ABANDONEDSTATION":
                        loadedCards.add(SldAbandonedStation.loadAbandonedStation(rootNode.get(i)));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<Integer, SldAdvCard> cardMap = new HashMap<>();
        for (SldAdvCard card : loadedCards){
            cardMap.put(card.getId(), card);
        }

        return cardMap;
    }

    /**
     * Delegates the drawing of the game circuit (player turn order) to the
     * {@link VisualFlyboardEasy} class, which handles the text-based UI representation.
     */
    @Override
    public void drawCircuit(){
        VisualFlyboardEasy visual = new VisualFlyboardEasy(this);
        visual.drawCircuit();
    }

    /**
     * Delegates the drawing of the scoreboard to the {@link VisualFlyboardEasy} class,
     * which handles the text-based UI representation.
     */
    @Override
    public void drawScoreboard(){
        VisualFlyboardEasy visual = new VisualFlyboardEasy(this);
        visual.drawScoreboard();
    }

    /**
     * Creates and initializes the game circuit (player turn order) for Easy mode.
     * In Easy mode, the circuit typically consists of 18 empty slots, indicating
     * the progression of rounds or phases without pre-defined player positions.
     *
     * @return A {@link List} of {@link Optional<Player>} representing the initial circuit.
     */
    @Override
    protected List<Optional<Player>> createCircuite(){
        List<Optional<Player>> newCircuite = new ArrayList<>();

        for (int i = 0; i < 18; i++)
            newCircuite.add(Optional.empty());

        return newCircuite;
    }

    /**
     * This method is a placeholder for building "little decks," a concept
     * typically used in Normal/Hard game modes for distributing components.
     * In Easy mode, components might be handled differently (e.g., open supply).
     */
    @Override
    protected void buildLittleDecks(){}

    /**
     * Throws an {@link IncorrectFlyBoardException} because "little decks" are not
     * a feature of the Easy game mode. This prevents accidental calls or misinterpretations.
     *
     * @return (Never returns)
     * @throws IncorrectFlyBoardException Always thrown as this feature is not supported.
     */
    @Override
    public List<List<Integer>> getLittleDecks(){
        throw new IncorrectFlyBoardException("No little decks available.");
    }

    /**
     * Throws an {@link IncorrectFlyBoardException} because "little decks" are not
     * a feature of the Easy game mode, and therefore cannot be set.
     *
     * @param decks The list of decks to set (unused).
     * @throws IncorrectFlyBoardException Always thrown as this feature is not supported.
     */
    @Override
    public List<Integer> getHiddenDeck() {
        throw new IncorrectFlyBoardException("No hidden deck available");
    }

    @Override
    public void buildAdventureDeck() {
        throw new IncorrectFlyBoardException("Not implemented yet");
    }
    @Override
    public void setLittleDecks(List<List<Integer>> decks){
        throw new IncorrectFlyBoardException("No little decks to set.");
    }

    /**
     * Retrieves the {@link ShipBoard} instance for a player based on their {@link HousingColor}.
     *
     * @param color The {@link HousingColor} of the player's ship.
     * @return The {@link ShipBoard} instance associated with the given color, or {@code null} if not found or supported.
     */
    @Override
    @Deprecated
    public ShipBoard getBuiltShip(HousingColor color){
        return null;
    }
}
