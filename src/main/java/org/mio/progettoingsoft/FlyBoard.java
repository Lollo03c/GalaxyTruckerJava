package org.mio.progettoingsoft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.HourGlass;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.components.*;


import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;

public class FlyBoard {
//    private  HourGlass hourGlass;

    private  List<AdventureCard> selectionDeckPrivate;
    private  List<AdventureCard> selectionDeck1;
    private  List<AdventureCard> selectionDeck2;
    private  List<AdventureCard> selectionDeck3;

    private  List<AdventureCard> deck;

    private  List<Optional<Player>> circuit;

    private  List<Player> scoryBoard;
    private final List<Component> coveredComponents;
    private  List<Component> uncoverdeComponents;

    private  Map<GoodType, Integer> remainingGoods;

    private HourGlass hourGlass;

    public FlyBoard(){
        this.coveredComponents = new ArrayList<>();
        this.uncoverdeComponents = new ArrayList<>();
        this.scoryBoard = new ArrayList<>();
        loadComponents();
        loadAdventureCard();
    }

    public Boolean addPlayer(String username){
        boolean toAdd =scoryBoard.stream().noneMatch(player -> player.getUsername().equals(username))
                && scoryBoard.size() < 4;

        if (toAdd){
            scoryBoard.add(new Player(username));
            return true;
        }

        return false;
    }

    public void StartGame(){

    }

    public void addGood(GoodType type, Integer quant){

    }

    public Boolean removeGood(GoodType type, Integer quant){
        return false;
    }

    public void moveRocket(Player player, int nPos){

    }

    public Player getPosition(int position){
        return new Player("");
    }

    public List<Player> getScoryBoard(){
        return scoryBoard;
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
                        List<GoodType> rewardList = new ArrayList<>();
                        JsonNode rewardNode = rootNode.get(i).path("reward");
                        for (JsonNode reward : rewardNode) {
                            rewardList.add(GoodType.stringToGoodType(reward.asText()));
                        }
                        int goodsLost = rootNode.get(i).path("goodsLost").asInt();
                        this.deck.add(new Smugglers(id, level, strength, daysLost, goodsLost, rewardList));
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
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
