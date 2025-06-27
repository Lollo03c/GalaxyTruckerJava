package org.mio.progettoingsoft.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.model.components.HousingColor;
import org.mio.progettoingsoft.model.advCards.sealed.*;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.server.GameManager;
import org.mio.progettoingsoft.utils.Logger;
import org.mio.progettoingsoft.views.tui.VisualFlyboardNormal;

import java.io.IOException;
import java.util.*;

/**
 * Represents the normal mode game board (FlyBoard) in the game.
 * This class extends {@link FlyBoard} and implements specific logic
 * for the normal game mode, including the management of an hourglass,
 * circuit creation, and loading of sealed adventure cards.
 */
public class FlyBoardNormal extends FlyBoard  {
    private Hourglass hourglass;

    /**
     * Constructs a new FlyBoardNormal instance for a normal game mode.
     * @param nicknames A {@link Set} of player nicknames participating in the game.
     */
    public FlyBoardNormal(Set<String> nicknames){
        super(GameMode.NORMAL, nicknames);
    }

    /**
     * Converts an internal circuit index to a player position on the visual circuit.
     * This mapping is specific to the normal game mode's visual representation.
     * @param index The internal index in the circuit list.
     * @return The corresponding visual position, or 0 if the index is incorrect.
     */
    public static int indexToPosition(int index) {
        int i;
        switch(index){
            case 0 -> i=4;
            case 1 -> i=3;
            case 3 -> i=2;
            case 6 -> i=1;
            default -> {
                i=0;
                System.out.println("Error in indexToPosition : incorrect index");
            }
        }
        return i;
    }

    /**
     * Converts a player's position on the visual circuit to an internal circuit index.
     * This mapping is specific to the normal game mode's visual representation.
     * @param position The visual position on the circuit.
     * @return The corresponding internal index, or -1 if the position is incorrect.
     */
    public static int positionToIndex(int position){
        int i;
        switch(position){
            case 4  -> i = 0;
            case 3  -> i = 1;
            case 2  -> i = 3;
            case 1  -> i = 6;
            default -> {
                i=-1;
                Logger.warning("Error in positionToIndex : incorrect index");
            }
        }
        return i;
    }

    /**
     * Draws the game circuit using the {@link VisualFlyboardNormal} for the normal game mode.
     */
    @Override
    public void drawCircuit(){
        VisualFlyboardNormal visual = new VisualFlyboardNormal(this);
        visual.drawCircuit();
    }

    /**
     * Draws the scoreboard using the {@link VisualFlyboardNormal} for the normal game mode.
     */
    @Override
    public void drawScoreboard(){
        VisualFlyboardNormal visual = new VisualFlyboardNormal(this);
        visual.drawScoreboard();
    }

    /**
     * Creates and initializes the game circuit with 24 empty positions.
     * @return A {@link List} of {@link Optional} Players representing the circuit,
     * with each position initially empty.
     */
    @Override
    protected List<Optional<Player>> createCircuite(){
        List<Optional<Player>> newCircuit = new ArrayList<>();

        for (int i = 0; i < 24; i++)
            newCircuit.add(Optional.empty());

        return newCircuit;
    }

    /**
     * Starts or restarts the hourglass timer for the current game.
     * If it's the first use, a new {@link Hourglass} instance is created.
     * @param idGame The ID of the ongoing game to which this hourglass belongs.
     */
    @Override
    public void startHourglass(int idGame) {
        if (firstUseHourglass){
            this.hourglass = new Hourglass(GameManager.getInstance().getOngoingGames().get(idGame));
            firstUseHourglass = false;
            hourglass.start();
        }
        else{
            hourglass.start();
        }
    }

    /**
     * Builds the "little decks" of adventure cards, distributing level 1 and level 2
     * cards into three separate decks. This is specific to the normal game mode's card distribution.
     */
    protected void buildLittleDecks(){
        List<Integer> copyDeck = new ArrayList<>(deck);

        littleDecks = new ArrayList<>();
        hiddenDeck = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            littleDecks.add(new ArrayList<>());

        int countLevel1 = 0;
        int countLevel2 = 0;

        List<Integer> levelOneCards = new ArrayList<>();
        int j = 0;
        for(int i = 0; i < copyDeck.size() && j < 4; i++){
            if(sldAdvCards.get(copyDeck.get(i)).getLevel() == 1){
                levelOneCards.add(copyDeck.remove(i));
                j++;
            }
        }

        List<Integer> levelTwoCards = new ArrayList<>();
        j = 0;
        for(int i = 0; i < copyDeck.size() && j < 9; i++){
            if(sldAdvCards.get(copyDeck.get(i)).getLevel() == 2){
                levelTwoCards.add(copyDeck.remove(i));
                j++;
            }
        }

        for (int i = 0; i < 3; i++)
            littleDecks.get(i).add(levelOneCards.get(i));

        for (int i = 0; i < 6; i++)
            littleDecks.get(i / 2).add(levelTwoCards.get(i));

        hiddenDeck.add(levelOneCards.get(3));
        hiddenDeck.add(levelTwoCards.get(7));
        hiddenDeck.add(levelTwoCards.get(8));
    }

    /**
     * Sets the "little decks" for the game. This method creates a deep copy
     * of the provided decks to ensure immutability.
     * @param decks A {@link List} of {@link List} of Integers, where each inner list
     * represents a little deck of card IDs.
     */
    @Override
    public void setLittleDecks(List<List<Integer>> decks){
        this.littleDecks = new ArrayList<>();
        for(List<Integer> deck : decks){
            littleDecks.add(new ArrayList<>(deck));
        }
    }

    /**
     * Returns a deep copy of the "little decks" currently in use.
     * @return A {@link List} of {@link List} of Integers, representing the little decks.
     */
    @Override
    public List<List<Integer>> getLittleDecks(){
        List<List<Integer>> decks = new ArrayList<>();
        for(List<Integer> deck : littleDecks){
            decks.add(new ArrayList<>(deck));
        }
        return decks;
    }

    @Override
    public List<Integer> getHiddenDeck(){
        return new ArrayList<>(hiddenDeck);
    }

    /**
     * Builds the actual deck for the adventure: it clears the deck and adds all the cards in little decks, then shuffles
     */
    public void buildAdventureDeck(){
        deck.clear();
        deck.addAll(hiddenDeck);
        for(List<Integer> lilDeck : littleDecks){
            deck.addAll(lilDeck);
        }
        Collections.shuffle(deck);
    }

    /**
     * Loads sealed adventure cards from a JSON resource file ("advCards.json").
     * It parses the JSON and creates appropriate {@link SldAdvCard} objects
     * based on their "type" field.
     * @return A {@link Map} where the key is the card ID and the value is the
     * corresponding {@link SldAdvCard} object.
     */
    @Override
    protected Map<Integer, SldAdvCard> loadSldAdvCard() {
        List<SldAdvCard> loadedCards = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            //File cardsFile = new File(getClass().getResource("/advCards.json").toExternalForm());
            JsonNode rootNode = mapper.readTree(getClass().getResourceAsStream("/advCards.json"));

            for (int i = 0; i < rootNode.size(); i++) {
                String type = rootNode.get(i).path("type").asText();

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

        Map<Integer, SldAdvCard> advCardsMap = new HashMap<>();
        for (SldAdvCard card : loadedCards){
            advCardsMap.put(card.getId(), card);
        }

        return advCardsMap;
    }

    public void createDeckForAdventure(){

    }

    /**
     * Retrieves a {@link ShipBoardNormal} instance built according to the specified
     * {@link HousingColor}. This method is synchronized to ensure thread-safe ship creation.
     * @param color The {@link HousingColor} of the ship to build (RED, YELLOW, GREEN, or BLUE).
     * @return The built {@link ShipBoardNormal} instance, or {@code null} if the color is not recognized.
     */


    public ShipBoard getBuiltShip(HousingColor color){
        synchronized (this) {
            return switch (color) {
                case RED -> ShipBoardNormal.buildRed(this);
                case YELLOW -> ShipBoardNormal.buildYellow(this);
                case GREEN -> ShipBoardNormal.buildFirst(this);
                case BLUE -> ShipBoardNormal.buildBlue(this);
                default -> null;
            };
        }
    }
}
