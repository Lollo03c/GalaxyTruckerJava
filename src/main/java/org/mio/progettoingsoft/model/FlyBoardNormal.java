package org.mio.progettoingsoft.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.views.tui.VisualFlyboardNormal;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FlyBoardNormal extends FlyBoard {

    public FlyBoardNormal(Set<String> nicknames){
        super(GameMode.NORMAL, nicknames);
    }


    @Override
    protected Map<Integer, AdventureCard> loadAdventureCard() {
        List<AdventureCard> loadedCards = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(new File("src/main/resources/advCards.json"));

            for (int i = 0; i < rootNode.size(); i++) {
                String type = rootNode.get(i).path("type").asText();

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

        Map<Integer, AdventureCard> advCardsMap = new HashMap<>();
        for (AdventureCard card : loadedCards){
            advCardsMap.put(card.getId(), card);
        }


        return advCardsMap;
    }
    @Override
    public void drawCircuit(){
        VisualFlyboardNormal visual = new VisualFlyboardNormal(this);
        visual.drawCircuit();
    }

    @Override
    public void drawScoreboard(){
        VisualFlyboardNormal visual = new VisualFlyboardNormal(this);
        visual.drawScoreboard();
    }

    @Override
    protected List<Optional<Player>> createCircuite(){
        List<Optional<Player>> newCircuit = new ArrayList<>();

        for (int i = 0; i < 24; i++)
            newCircuit.add(Optional.empty());

        return newCircuit;
    }

    protected void buildLittleDecks(){
        List<Integer> copyDeck = new ArrayList<>(deck);

        littleDecks = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            littleDecks.add(new ArrayList<>());

        int countLevel1 = 0;
        int countLevel2 = 0;

        List<Integer> levelOneCards = copyDeck.stream()
                .filter(id -> adventureCards.get(id).getLevel() == 1)
                .toList();

        for (int i = 0; i < 3; i++)
            littleDecks.get(i).add(levelOneCards.get(i));

        List<Integer> levelTwoCards = copyDeck.stream()
                .filter(id -> adventureCards.get(id).getLevel() == 2)
                .toList();

        for (int i = 0; i < 6; i++)
            littleDecks.get(i / 2).add(levelTwoCards.get(i));


    }
}
