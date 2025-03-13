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
        loadAdventureCard();

        this.circuit = new ArrayList<>(24);

        for (int i = 0; i < 24; i++)
            circuit.add(Optional.empty());
    }
    public void drawAcard(){
        AdventureCard card = deck.getFirst();
        deck.removeFirst();
        card.start(FlyBoard.this);
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

    public void moveRocket(Player player, int nPos){

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
            final int nComponents = rootNode.size();

            for (int i = 0; i < nComponents; i++) {
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

            //System.out.println("First type : " + type);

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
//advance one step and if necessary update the scoryboard
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
                int position2 = scoreBoard.indexOf(player2);
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

    public void loadAdventureCard(){

        ObjectMapper mapper = new ObjectMapper();

        try{
            JsonNode rootNode = mapper.readTree(new File("src/main/resources/advCards.json"));
            final int nCards = rootNode.size();

            for (int i = 0; i < nCards; i++) {
                int id = rootNode.get(i).path("id").asInt();
                int level = rootNode.get(i).path("level").asInt();
                String type = rootNode.get(i).path("type").asText();

                switch (type) {
                    case "SLAVERS": {
                        int strength = rootNode.get(i).path("strength").asInt();
                        int daysLost = rootNode.get(i).path("daysLost").asInt();
                        int reward = rootNode.get(i).path("reward").asInt();
                        int crewLost = rootNode.get(i).path("crewLost").asInt();
                        this.deck.add(new Slaver(id, level, strength, daysLost, reward, crewLost));
                    }
                    break;

                    case "SMUGGLERS": {
                        int strength = rootNode.get(i).path("strength").asInt();
                        int daysLost = rootNode.get(i).path("daysLost").asInt();
                        List<GoodType> rewards = new ArrayList<>();
                        JsonNode rewardNode = rootNode.get(i).path("reward");
                        for (JsonNode reward : rewardNode) {
                            rewards.add(GoodType.stringToGoodType(reward.asText()));
                        }
                        int goodsLost = rootNode.get(i).path("goodsLost").asInt();
                        this.deck.add(new Smugglers(id, level, strength, daysLost, goodsLost, rewards));
                    }
                    break;

                    case "PIRATE": {
                        int strength = rootNode.get(i).path("strength").asInt();
                        int daysLost = rootNode.get(i).path("daysLost").asInt();
                        List<CannonPenalty> cannons = new ArrayList<>();
                        JsonNode cannonsNode = rootNode.get(i).path("cannons");
                        for (JsonNode cannon : cannonsNode) {
                            cannons.add(CannonPenalty.stringToCannonPenalty(cannon.get(1).asText(),cannon.get(0).asText()));
                        }
                        int reward = rootNode.get(i).path("reward").asInt();
                        this.deck.add(new Pirate(id, level, strength, daysLost, cannons, reward));
                    }
                    break;

                    case "STARDUST":
                        this.deck.add(new Stardust(id, level));
                        break;

                    case "EPIDEMIC":
                        this.deck.add(new Stardust(id, level));
                        break;

                    case "OPENSPACE":
                        this.deck.add(new OpenSpace(id, level));
                        break;

                    case "METEORSWARM": {
                        List<Meteor> meteors = new ArrayList<>();
                        JsonNode meteorsNode = rootNode.get(i).path("meteors");
                        for(JsonNode meteor : meteorsNode) {
                            meteors.add(Meteor.stringToMeteor(meteor.get(1).asText(),meteor.get(0).asText()));
                        }
                        this.deck.add(new MeteorSwarm(id, level, meteors));
                    }
                    break;

                    case "PLANETS": {
                        int daysLost = rootNode.get(i).path("daysLost").asInt();
                        List<Planet> planets = new ArrayList<>();
                        JsonNode planetsNode = rootNode.get(i).path("planets");
                        for(JsonNode planet : planetsNode) {
                            planets.add(Planet.stringToPlanet(planet));
                        }
                        this.deck.add(new Planets(id, level, daysLost, planets));
                    }
                    break;

                    case "COMBATZONE": {
                        List<CombatLine> combatLines = new ArrayList<>();
                        List<Penalty> cannonPenalties = new ArrayList<>();
                        JsonNode criterionsNode = rootNode.get(i).path("criterion");
                        JsonNode penaltyNode = rootNode.get(i).path("penalty");
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
                        this.deck.add(new CombatZone(id, level, combatLines));
                    }
                    break;

                    case "ABANDONEDSHIP": {
                        int daysLost = rootNode.get(i).path("daysLost").asInt();
                        int credits = rootNode.get(i).path("credits").asInt();
                        int crewLost = rootNode.get(i).path("crewLost").asInt();
                        this.deck.add(new AbandonedShip(id, level, daysLost, credits, crewLost));
                    }
                    break;

                    case "ABANDONEDSTATION": {
                        int daysLost = rootNode.get(i).path("daysLost").asInt();
                        int crewNeeded = rootNode.get(i).path("crewNeeded").asInt();
                        List<GoodType> goods = new ArrayList<>();
                        JsonNode goodsNode = rootNode.get(i).path("goods");
                        for (JsonNode good : goodsNode) {
                            goods.add(GoodType.stringToGoodType(good.asText()));
                        }
                        this.deck.add(new AbandonedStation(id, level, goods));
                    }
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
