package org.mio.progettoingsoft.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.advCards.sealed.*;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.views.tui.VisualFlyboardEasy;
import org.mio.progettoingsoft.views.tui.VisualFlyboardNormal;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FlyBoardEasy extends FlyBoard {

    public FlyBoardEasy(Set<String> nicknames){
        super(GameMode.EASY, nicknames);
    }

    @Override
    public void startHourglass(int idGame) {

    }

    @Override
    protected Map<Integer, AdventureCard > loadAdventureCard() {
        List<AdventureCard> loadedCards = new ArrayList<>();

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
                        loadedCards.add(Slaver.loadSlaver(rootNode.get(i)));
                        break;

                    case "SMUGGLERS":
                        loadedCards.add(Smugglers.loadSmugglers(rootNode.get(i)));
                        break;

                    case "PIRATE":
                        loadedCards.add(Pirate.loadPirate(rootNode.get(i)));
                        break;

                    case "STARDUST":
                        loadedCards.add(Stardust.loadStardust(rootNode.get(i)));
                        break;

                    case "EPIDEMIC":
                        loadedCards.add(Epidemic.loadEpidemic(rootNode.get(i)));
                        break;

                    case "OPENSPACE":
                        loadedCards.add(OpenSpace.loadOpenSpace(rootNode.get(i)));
                        break;

                    case "METEORSWARM":
                        loadedCards.add(MeteorSwarm.loadMeteorSwarm(rootNode.get(i)));
                        break;

                    case "PLANETS":
                        loadedCards.add(Planets.loadPlanets(rootNode.get(i)));
                        break;

                    case "COMBATZONE":
                        loadedCards.add(CombatZone.loadCombatZone(rootNode.get(i)));
                        break;

                    case "ABANDONEDSHIP":
                        loadedCards.add(AbandonedShip.loadAbandonedShip(rootNode.get(i)));
                        break;

                    case "ABANDONEDSTATION":
                        loadedCards.add(AbandonedStation.loadAbandonedStation(rootNode.get(i)));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<Integer, AdventureCard> cardMap = new HashMap<>();
        for (AdventureCard card : loadedCards){
            cardMap.put(card.getId(), card);
        }

        return cardMap;
    }

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

    @Override
    public void drawCircuit(){
        VisualFlyboardEasy visual = new VisualFlyboardEasy(this);
        visual.drawCircuit();
    }
    @Override
    public void drawScoreboard(){
        VisualFlyboardEasy visual = new VisualFlyboardEasy(this);
        visual.drawScoreboard();
    }

    @Override
    protected List<Optional<Player>> createCircuite(){
        List<Optional<Player>> newCircuite = new ArrayList<>();

        for (int i = 0; i < 18; i++)
            newCircuite.add(Optional.empty());

        return newCircuite;
    }

    @Override
    protected void buildLittleDecks(){

    }

    @Override
    public List<List<Integer>> getLittleDecks(){
        throw new IncorrectFlyBoardException("No little decks available");
    }

    @Override
    public void setLittleDecks(List<List<Integer>> decks){
        throw new IncorrectFlyBoardException("No little decks to set");
    }

    @Override
    public ShipBoard getBuiltShip(HousingColor color){
        return null;
    }
}
