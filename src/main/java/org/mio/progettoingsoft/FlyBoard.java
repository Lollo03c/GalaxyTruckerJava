package org.mio.progettoingsoft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.HourGlass;
import org.mio.progettoingsoft.Player;
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
        loadComponents();
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
        return new LinkedList<>();
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

                    case "HOUSING": {
                        this.coveredComponents.add(new Housing(id, top, bottom, right, left));
                    }
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
}
