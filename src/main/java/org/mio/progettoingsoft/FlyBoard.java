package org.mio.progettoingsoft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.components.*;


import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;

public class FlyBoard {
    private  List<AdventureCard> selectionDeckPrivate;
    private  List<AdventureCard> selectionDeck1;
    private  List<AdventureCard> selectionDeck2;
    private  List<AdventureCard> selectionDeck3;

    private final List<AdventureCard> deck;

    private final List<Optional<Player>> circuit;

    private final List<Player> scoreBoard;
    private final List<Component> coveredComponents;
    private final List<Component> uncoverdeComponents;

    private final Map<GoodType, Integer> remainingGoods;

    private HourGlass hourGlass;

    public FlyBoard(){
        this.coveredComponents = new ArrayList<>();
        this.uncoverdeComponents = new ArrayList<>();
        this.scoreBoard = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.remainingGoods = new HashMap<>();

        loadComponents();
        loadAdventureCards();

        this.circuit = new ArrayList<>(24);

        for (int i = 0; i < 24; i++)
            circuit.add(Optional.empty());
    }

    public void addPlayer(Player player){
        scoreBoard.add(player);
    }

    public Boolean addPlayer(String username){
        boolean toAdd = scoreBoard.stream().noneMatch(player -> player.getUsername().equals(username))
                && scoreBoard.size() < 4;

        if (toAdd){
            scoreBoard.add(new Player(username));
            return true;
        }

        return false;
    }

    public void StartGame(){

    }

    public List<Optional<Player>> getCircuit(){
        return  circuit;
    }

    public Player getPosition(int position){
        return new Player("");
    }

    public List<Player> getScoreBoard(){
        return scoreBoard;
    }

    public void playAdventureCard(){

    }

    public List<Component> getCoveredComponents(){
        return coveredComponents;
    }

    public void loadComponents() {

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(new File("src/main/resources/components.json"));

            for (int i = 0; i < rootNode.size(); i++) {
                String type = rootNode.get(i).path("type").asText();
                int id = rootNode.get(i).path("id").asInt();
                Connector top = Connector.stringToConnector(rootNode.get(i).path("top").asText());
                Connector left = Connector.stringToConnector(rootNode.get(i).path("left").asText());
                Connector bottom = Connector.stringToConnector(rootNode.get(i).path("bottom").asText());
                Connector right = Connector.stringToConnector(rootNode.get(i).path("right").asText());

                switch (type) {
                    case "ENERGY_DEPOT": {
                        boolean isTriple = rootNode.get(i).path("kind").asInt() == 3;
                        this.coveredComponents.add(new EnergyDepot(id, isTriple, top, bottom, right, left));
                    }
                    break;

                    case "DEPOT": {
                        boolean isBig = rootNode.get(i).path("isBig").asBoolean();
                        boolean isHazard = rootNode.get(i).path("isHazard").asBoolean();
                        this.coveredComponents.add(new Depot(id, isBig, isHazard, top, bottom, right, left));
                    }
                    break;

                    case "HOUSING":
                        this.coveredComponents.add(new Housing(id, top, bottom, right, left));
                    break;

                    case "PIPE":
                        this.coveredComponents.add(new Pipe(id, top, bottom, right, left));
                        break;

                    case "ENGINE":
                        this.coveredComponents.add(new Engine(id, top, bottom, right, left));
                        break;

                    case "DOUBLE_ENGINE":
                        this.coveredComponents.add(new DoubleEngine(id, top, bottom, right, left));
                        break;
                    case "DRILL":
                        this.coveredComponents.add(new Drill(id, top, bottom, right, left));
                        break;
                    case "DOUBLE_DRILL":
                        this.coveredComponents.add(new DoubleDrill(id, top, bottom, right, left));
                        break;
                    case "ALIEN_HOUSING":
                        AlienType color = AlienType.stringToAlienType(rootNode.get(i).path("color").asText());
                        this.coveredComponents.add(new AlienHousing(id, color, top, bottom, right, left));
                        break;
                    case "SHIELD":
                        this.coveredComponents.add(new Shield(id, top, bottom, right, left));
                        break;
                }

                Collections.shuffle(coveredComponents);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    private  List<Optional<Player>> circuit;
    //    list da 24 celle
    public void moveDays(Player player, int days){
        boolean advance = days > 0 ? true : false;

        if (advance){
            for(int i = 0; i<days; i++)
                advanceOne(player);
        }
        else{
            for(int i = days; i < 0; i++)
                retreatOne(player);
        }
    }

    //advance one step and if necessary update the scoreboard
    private void advanceOne(Player player){
        int start = circuit.indexOf(Optional.of(player));
        int index = start;

        do {
            index++;
            if (index == 24)
                index = 0;
            Optional<Player> player2 =circuit.get(index);
            if(player2.isPresent()){
                int position1 = scoreBoard.indexOf(player);
                int position2 = scoreBoard.indexOf(player2.get());
                if(position1 > position2) {
                    scoreBoard.set(position1, player2.orElse(player));
                    scoreBoard.set(position2, player);
                }
                else{
                    //player2 viene doppiato e quindi eliminato(?)
                    //no player2 viene doppiato e quindi rimane fermo dove si trova, semplicemente viene superata da player
                }
            }
        }
        while (circuit.get(index).isPresent());

        circuit.set(start, Optional.empty());
        circuit.set(index, Optional.of(player));
    }

    private void retreatOne(Player player){
        int start = circuit.indexOf(Optional.of(player));
        int index = start;

        do {
            index--;
            if (index == -1)
                index = 23;
            Optional<Player> player2 =circuit.get(index);
            if(player2.isPresent()){
                int position1 = scoreBoard.indexOf(player);
                int position2 = scoreBoard.indexOf(player2.get());
                if(position1 < position2){
                    scoreBoard.set(position1, player2.orElse(player));
                    scoreBoard.set(position2, player);
                }
                else{
                    //player viene doppiato da player2 e quindi player viene eliminato
                }
            }
        }
        while (circuit.get(index).isPresent());

        circuit.set(start, Optional.empty());
        circuit.set(index, Optional.of(player));
    }

    public List<AdventureCard> getAdventureCards(){return deck;}

    public void loadAdventureCards(){

        ObjectMapper mapper = new ObjectMapper();

        try{
            JsonNode rootNode = mapper.readTree(new File("src/main/resources/advCards.json"));

            for (int i = 0; i < rootNode.size(); i++) {
                String type = rootNode.get(i).path("type").asText();

                switch (type) {
                    case "SLAVERS":
                        this.deck.add(Slaver.loadSlaver(rootNode.get(i)));
                    break;

                    case "SMUGGLERS":
                        this.deck.add(Smugglers.loadSmugglers(rootNode.get(i)));
                    break;

                    case "PIRATE":
                        this.deck.add(Pirate.loadPirate(rootNode.get(i)));
                    break;

                    case "STARDUST":
                        this.deck.add(Stardust.loadStardust(rootNode.get(i)));
                    break;

                    case "EPIDEMIC":
                        this.deck.add(Epidemic.loadEpidemic(rootNode.get(i)));
                    break;

                    case "OPENSPACE":
                        this.deck.add(OpenSpace.loadOpenSpace(rootNode.get(i)));
                    break;

                    case "METEORSWARM":
                        this.deck.add(MeteorSwarm.loadMeteorSwarm(rootNode.get(i)));
                    break;

                    case "PLANETS":
                        this.deck.add(Planets.loadPlanets(rootNode.get(i)));
                    break;

                    case "COMBATZONE":
                        this.deck.add(CombatZone.loadCombatZone(rootNode.get(i)));
                    break;

                    case "ABANDONEDSHIP":
                        this.deck.add(AbandonedShip.loadAbandonedShip(rootNode.get(i)));
                    break;

                    case "ABANDONEDSTATION":
                        this.deck.add(AbandonedStation.loadAbandonedStation(rootNode.get(i)));
                    break;
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void shuffleDeck(){
        Collections.shuffle(deck);
    }

    public AdventureCard drawAdventureCard(){
        AdventureCard card = deck.remove(0);
        return card;
    }

    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }
}
