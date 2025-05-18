package org.mio.progettoingsoft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.components.*;


import java.io.File;
import java.io.IOException;

import org.mio.progettoingsoft.exceptions.CannotAddPlayerException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.NoMoreComponentsException;
import org.mio.progettoingsoft.model.FlyBoardEasy;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.FlyBoardServer;

import java.util.*;

/**
 * The Flyboard
 *
 * has to be created staticty with FlyBoard.createFlyBorard(GameMode) base of the mode of the game to start
 *
 */

public abstract class FlyBoard implements FlyBoardServer {
    GameState state;
    private String messageToSend;

    private final GameMode mode;

    private List<AdventureCard> selectionDeckPrivate;
    private List<AdventureCard> selectionDeck1;
    private List<AdventureCard> selectionDeck2;
    private List<AdventureCard> selectionDeck3;

    protected final List<AdventureCard> deck;

    public final List<Optional<Player>> circuit;
    private List<Player> scoreBoard = new ArrayList<>();
    protected final List<Integer> coveredComponents;
    private final List<Integer> uncoveredComponents;

    protected final List<Player> players;

    private Map<Integer, Component> components = loadComponentMap();

    private HourGlass hourGlass;



    protected FlyBoard(GameMode mode, Set<String> nicknames) {
        this.mode = mode;
        this.coveredComponents = new ArrayList<>();

        coveredComponents.addAll(components.keySet());
        Collections.shuffle(coveredComponents);


        deck = loadAdventureCard();

        uncoveredComponents = new ArrayList<>();
        players = new ArrayList<>(nicknames.size());

        Iterator<HousingColor> colorIter = Arrays.asList(HousingColor.values()).iterator();
        for (String nick : nicknames){
            players.add(new Player(nick, colorIter.next(), mode, this));
        }

        circuit = createCircuite();

    }

    /**
     *
     * @param mode the {@link GameMode} with which start the fame
     * @param nicknames the nicknames of the players
     * @return the {@link FlyBoard} created
     */
    public static FlyBoard createFlyBoard(GameMode mode, Set<String> nicknames) {
        FlyBoard flyBoard = null;
        switch (mode) {
            case EASY -> flyBoard = new FlyBoardEasy(nicknames);
            case NORMAL -> flyBoard = new FlyBoardNormal(nicknames);
        }

        return flyBoard;
    }

    /**
     * GETTER
     */
    public Player getPlayerByUsername(String nickname) throws IncorrectFlyBoardException {
        for (Player player : players)
            if (player.getNickname().equals(nickname))
                return player;

        throw new IncorrectFlyBoardException("");
    }

    public Optional<Player> getPlayerByColor(HousingColor colorPlayerEnum){
        return scoreBoard.stream()
                .filter(p -> p.getColor().equals(colorPlayerEnum))
                .findFirst();
    }

    public List<Optional<Player>> getCircuit() {
        return circuit;
    }

    public List<Player> getScoreBoard() {
        return scoreBoard;
    }

    public GameState getState(){
        return state;
    }

    public List<Integer> getCoveredComponents() {
        return coveredComponents;
    }

    public List<Integer> getUncoveredComponents() {
        return uncoveredComponents;
    }

    public Component getComponentById(int idComp){
        return components.get(idComp);
    }

    public Integer drawComponent() throws NoMoreComponentsException {
        if (coveredComponents.isEmpty())
            throw new NoMoreComponentsException("Covered components are not enough");
        return coveredComponents.removeLast();
    }
    /**
    *   Method that draws the circuit
     */
    public void drawCircuit(){}
    public void drawScoreboard(){

    }
    public void addUncoveredComponent(int c) {
        this.uncoveredComponents.add(c);
    }

    public Integer chooseComponentFromUncoveredByIndex(int index) throws NoMoreComponentsException {
        if (uncoveredComponents.isEmpty())
            throw new NoMoreComponentsException("No more uncovered components.");

        return uncoveredComponents.remove(index);
    }

    public Component chooseComponentFromUncoveredById(int id) throws NoMoreComponentsException {
        if (uncoveredComponents.isEmpty())
            throw new NoMoreComponentsException("No more uncovered components.");

        Component comp = getComponentById(uncoveredComponents.stream().filter(c -> c == id).findFirst().get());
        boolean removed = uncoveredComponents.remove(comp);
        if (removed)
            return comp;
        throw new NoMoreComponentsException("It's not possible to remove the component.");
    }

    public List<AdventureCard> getAdventureCards() {
        return deck;
    }

    // adds a player with the passed user and color (for the main housing), throws an exc if necessary
    public void addPlayer(String username, HousingColor color) throws CannotAddPlayerException {

        if (scoreBoard.stream().anyMatch(player -> player.getNickname().equals(username)))
            throw new CannotAddPlayerException("Cannot add player with username " + username + ". Username already in use");
        if (scoreBoard.stream().anyMatch(player -> player.getColor().equals(color)))
            throw new CannotAddPlayerException("Cannot add player with color " + color + ". Color already in use");
        if (scoreBoard.size() == 4)
            throw new CannotAddPlayerException("Cannot add player. The game is full");

        scoreBoard.add(new Player(username, color, mode, this));

        if (scoreBoard.size() == 4){
            state = GameState.BUILDING_SHIP;
        }
    }


    // adds a player to the circuit: it must be used ONLY for initialization
//    public void addPlayerToCircuit(String username, int index){
//        if(index > getCircuit().size() || index < 0){
//            throw new BadParameterException("Index out of circuit range");
//        }
//        if(this.getPlayerByUsername(username).isEmpty()){
//            throw new RuntimeException("This player doesn't exist");
//        }
//        if(getCircuit().get(index).isPresent()){
//            throw new RuntimeException("This place is occupied");
//        }
//        if(this.getPlayerByUsername(username).get().isRunning()){
//            throw new RuntimeException("This player is already running");
//        }
//        Player p = this.getPlayerByUsername(username).get();
//        this.getCircuit().add(index, Optional.of(p));
//        int numPlayersAhead = 0;
//        for(int i = index + 1; i < getCircuit().size(); i++){
//            if(getCircuit().get(i).isPresent()){
//                numPlayersAhead++;
//            }
//        }
//        this.getScoreBoard().remove(p);
//        this.getScoreBoard().add(numPlayersAhead, p);
//        this.getPlayerByUsername(username).get().setRunning(true);
//    }

    //    private  List<Optional<Player>> circuit;
    //    list da 24 celle
    public void moveDays(Player player, int days) {
        boolean advance = days > 0 ? true : false;

        if (advance) {
            for (int i = 0; i < days; i++)
                advanceOne(player);
        } else {
            for (int i = days; i < 0; i++)
                retreatOne(player);
        }
    }

    //advance one step and if necessary send the scoreboard
    private void advanceOne(Player player) {
        int start = circuit.indexOf(Optional.of(player));
        int index = start;

        do {
            index++;
            if (index == 24)
                index = 0;
            Optional<Player> player2 = circuit.get(index);
            if (player2.isPresent()) {
                int position1 = scoreBoard.indexOf(player);
                int position2 = scoreBoard.indexOf(player2.get());
                if (position1 > position2) {
                    scoreBoard.set(position1, player2.orElse(player));
                    scoreBoard.set(position2, player);
                } else {
                    //player2 viene doppiato e quindi eliminato(?)
                    //no player2 viene doppiato e quindi rimane fermo dove si trova, semplicemente viene superata da player
                }
            }
        }
        while (circuit.get(index).isPresent());

        circuit.set(start, Optional.empty());
        circuit.set(index, Optional.of(player));
    }

    private void retreatOne(Player player) {
        int start = circuit.indexOf(Optional.of(player));
        int index = start;

        do {
            index--;
            if (index == -1)
                index = 23;
            Optional<Player> player2 = circuit.get(index);
            if (player2.isPresent()) {
                int position1 = scoreBoard.indexOf(player);
                int position2 = scoreBoard.indexOf(player2.get());
                if (position1 < position2) {
                    scoreBoard.set(position1, player2.orElse(player));
                    scoreBoard.set(position2, player);
                } else {
                    //player viene doppiato da player2 e quindi player viene eliminato
                }
            }
        }
        while (circuit.get(index).isPresent());

        circuit.set(start, Optional.empty());
        circuit.set(index, Optional.of(player));
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public AdventureCard drawAdventureCard() {
        AdventureCard card = deck.remove(0);
        return card;
    }

    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }


    /**
     *
     * @return the list of all the components loaded by JSON file
     */
    private Map<Integer, Component> loadComponentMap() {
        List<Component> loadedComponents = new ArrayList<>();

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
                        loadedComponents.add(new EnergyDepot(id, isTriple, top, bottom, right, left));
                    }
                    break;

                    case "DEPOT": {
                        boolean isBig = rootNode.get(i).path("isBig").asBoolean();
                        boolean isHazard = rootNode.get(i).path("isHazard").asBoolean();
                        loadedComponents.add(new Depot(id, isBig, isHazard, top, bottom, right, left));
                    }
                    break;

                    case "HOUSING":
                        loadedComponents.add(new Housing(id, top, bottom, right, left));
                        break;

                    case "PIPE":
                        loadedComponents.add(new Pipe(id, top, bottom, right, left));
                        break;

                    case "ENGINE":
                        loadedComponents.add(new Engine(id, top, bottom, right, left));
                        break;

                    case "DOUBLE_ENGINE":
                        loadedComponents.add(new DoubleEngine(id, top, bottom, right, left));
                        break;
                    case "DRILL":
                        loadedComponents.add(new Drill(id, top, bottom, right, left));
                        break;
                    case "DOUBLE_DRILL":
                        loadedComponents.add(new DoubleDrill(id, top, bottom, right, left));
                        break;
                    case "ALIEN_HOUSING":
                        GuestType color = GuestType.stringToAlienType(rootNode.get(i).path("color").asText());
                        loadedComponents.add(new AlienHousing(id, color, top, bottom, right, left));
                        break;
                    case "SHIELD":
                        loadedComponents.add(new Shield(id, top, bottom, right, left));
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        loadedComponents.add(new Housing(33, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE));
        loadedComponents.add(new Housing(34, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE));
        loadedComponents.add(new Housing(52, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE));
        loadedComponents.add(new Housing(61, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE));

        Map<Integer, Component> map = new HashMap<>();
        for (Component comp : loadedComponents){
            map.put(comp.getId(), comp);
        }

        return map;
    }

    /**
     *
     * @return the list of all the Adventure Cards based on the {@link GameMode} of the game
     */
    protected abstract List<AdventureCard> loadAdventureCard();

    /**
     *
     * @return the created circuite base on the {@link GameMode} of the game
     */
    protected abstract List<Optional<Player>> createCircuite();

    public void setScoreboard(List<Player> players) {
        this.scoreBoard = players;
    }

    public void setState(GameState state){
        this.state = state;
    }

    public List<Player> getPlayers(){
        return players;
    }

}
