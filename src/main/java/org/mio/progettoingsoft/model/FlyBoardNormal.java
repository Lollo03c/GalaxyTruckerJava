package org.mio.progettoingsoft.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.views.tui.VisualFlyboardNormal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FlyBoardNormal extends FlyBoard {

    public FlyBoardNormal(Set<String> nicknames){
        super(GameMode.NORMAL, nicknames);
    }


    @Override
    protected List<AdventureCard> loadAdventureCard() {
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

        return loadedCards;
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
}
