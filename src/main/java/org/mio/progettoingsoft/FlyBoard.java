package org.mio.progettoingsoft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.components.*;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.CannotAddPlayerException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.NoMoreComponentsException;
import org.mio.progettoingsoft.model.FlyBoardEasy;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.Hourglass;
import org.mio.progettoingsoft.model.ShipBoardNormal;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.model.events.LeavePlayerEvent;
import org.mio.progettoingsoft.model.events.MovePlayerEvent;
import org.mio.progettoingsoft.model.events.SetStateEvent;
import org.mio.progettoingsoft.model.interfaces.FlyBoardServer;
import org.mio.progettoingsoft.utils.Logger;

import javax.swing.text.html.Option;
import java.util.*;

/**
 * The Flyboard
 *
 * has to be created staticty with FlyBoard.createFlyBorard(GameMode) base of the mode of the game to start
 *
 */

public abstract class FlyBoard implements FlyBoardServer {
    private final GameMode mode;

    protected List<List<Integer>> littleDecks;
    protected List<Integer> hiddenDeck;
    private List<Integer> availableDecks = new ArrayList<>();

    protected final List<Integer> deck;

    protected boolean firstUseHourglass = true;
    public final List<Optional<Player>> circuit;
    private List<Player> scoreBoard = new ArrayList<>();
    protected final List<Integer> coveredComponents;
    private final List<Integer> uncoveredComponents;

    protected final List<Player> players;
    private List<Player> validationPlayers;
    private List<Player> addCrewPlayers;

    private Map<Integer, Component> components = loadComponentMap();
    protected Map<Integer, SldAdvCard> sldAdvCards;

    private SldAdvCard playedCard;
    private List<Player> waitingPlayers;
    private Map<GoodType, Integer> priceGoods = new EnumMap<>(GoodType.class);

    private boolean playedFirstCard = false;

    private final List<Integer> availableConstructedShips;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public PropertyChangeSupport getSupport() {
        return support;
    }

    protected FlyBoard(GameMode mode, Set<String> nicknames) {
        this.mode = mode;
        this.coveredComponents = new ArrayList<>();
        priceGoods.put(GoodType.RED, 4);
        priceGoods.put(GoodType.YELLOW, 3);
        priceGoods.put(GoodType.GREEN, 2);
        priceGoods.put(GoodType.BLUE, 1);
        coveredComponents.addAll(components.keySet());
        coveredComponents.removeAll(List.of(33, 34, 52, 61));

        if (mode.equals(GameMode.EASY)){
            for (Integer i = 137; i <= 148; i++){
                coveredComponents.remove(i);
            }
        }

        Collections.shuffle(coveredComponents);

        sldAdvCards = loadSldAdvCard();
        deck = new ArrayList<>();
        deck.addAll(sldAdvCards.keySet());
        //deck.addAll(adventureCards.keySet());
        Collections.shuffle(deck);

        buildLittleDecks();
        availableDecks.addAll(List.of(0, 1, 2));

        uncoveredComponents = new ArrayList<>();
        players = new ArrayList<>(nicknames.size());

//        Iterator<HousingColor> colorIter = Arrays.asList(HousingColor.values()).iterator();

        List<String> nickList = nicknames.stream().sorted().toList();
        List<HousingColor> ordered = HousingColor.getSorted();
        for (int i = 0; i < nicknames.size(); i++){
            players.add(new Player(nickList.get(i), HousingColor.getSorted().get(i), mode, this));
        }

        circuit = createCircuite();

        availableConstructedShips = new ArrayList<>();
        availableConstructedShips.addAll(List.of(1, 2, 3, 4));

    }

    public abstract void startHourglass(int idGame);

    protected abstract void buildLittleDecks();
    public abstract void setLittleDecks(List<List<Integer>> decks);
    public abstract List<List<Integer>> getLittleDecks();
    public abstract List<Integer> getHiddenDeck();
    public abstract void buildAdventureDeck();
    public List<Integer> getDeck(){
        return deck;
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

        throw new IncorrectFlyBoardException("No such player with this nickname: " + nickname);
    }

    public int getNumPlayers(){
        return players.size();
    }

    public List<Optional<Player>> getCircuit() {
        return circuit;
    }

    public List<Integer> getAvailableStartingPositions(){
        List<Integer> availablePlaces = new ArrayList<>();
        synchronized (this.getCircuit()) {
            List<Optional<Player>> circ = this.getCircuit();
            for (Integer i : new ArrayList<>(List.of(0, 1, 3, 6))) {
                if (circ.get(i).isEmpty()) {
                    availablePlaces.add(i);
                }
            }
        }
        return availablePlaces;
    }

    public int getPlayerPositionOnCircuit(String nickname){
        return circuit.indexOf(Optional.of(getPlayerByUsername(nickname)));
    }

    public boolean isReadyToAdventure(){
        return this.getScoreBoard().size() == this.getNumPlayers();
    }

    public List<Player> getScoreBoard() {
        return scoreBoard;
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

    public List<String> getNicknameList() {
        List<String> usernameList = new ArrayList<>();
        for(Player player : players){
            usernameList.add(player.getNickname());
        }
        return usernameList;
    }

    public Integer drawComponent() throws IncorrectFlyBoardException {
        if (coveredComponents.isEmpty())
            throw new IncorrectFlyBoardException("Covered components are not enough");
        return coveredComponents.removeLast();
    }
    /**
    *   Method that draws the circuit
     */
    public void drawCircuit(){}
    public void drawScoreboard(){

    }
    public void addUncoveredComponent(int c) {
        synchronized (uncoveredComponents) {
            this.uncoveredComponents.add(c);
        }
    }

    public SldAdvCard getSldAdvCardByID(int id) {
        return sldAdvCards.get(id);
    }

//    // adds a player with the passed user and color (for the main housing), throws an exc if necessary
//    public void addPlayer(String username, HousingColor color) throws CannotAddPlayerException {
//
//        if (scoreBoard.stream().anyMatch(player -> player.getNickname().equals(username)))
//            throw new CannotAddPlayerException("Cannot add player with username " + username + ". Username already in use");
//        if (scoreBoard.stream().anyMatch(player -> player.getColor().equals(color)))
//            throw new CannotAddPlayerException("Cannot add player with color " + color + ". Color already in use");
//        if (scoreBoard.size() == 4)
//            throw new CannotAddPlayerException("Cannot add player. The game is full");
//
//        scoreBoard.add(new Player(username, color, mode, this));
//
//        if (scoreBoard.size() == 4){
//            state = GameState.BUILDING_SHIP;
//        }
//    }


    // adds a player to the circuit: it must be used ONLY for initialization
    public synchronized void addPlayerToCircuit(String nickname, int index){
        if(index > getCircuit().size() || index < 0){
            throw new BadParameterException("Index out of circuit range");
        }

        Player player;
        player = getPlayerByUsername(nickname);

        if(getCircuit().get(index).isPresent()){
            throw new BadParameterException("This place is occupied");
        }
        if(player.isRunning()){
            throw new BadParameterException("This player is already running");
        }
        this.getCircuit().set(index, Optional.of(player));
        int numPlayersAhead = 0;
        for(int i = index + 1; i < getCircuit().size(); i++){
            if(getCircuit().get(i).isPresent()){
                numPlayersAhead++;
            }
        }
        this.getScoreBoard().remove(player);
        this.getScoreBoard().add(numPlayersAhead, player);
        player.setRunning(true);
    }

    //    private  List<Optional<Player>> circuit;
    //    list da 24 celle
    public void moveDays(Player player, int days) {
        Logger.debug("Moving " + days + " days from " + player.getNickname());
        boolean advance = days > 0 ? true : false;

        if (advance) {
            for (int i = 0; i < days; i++)
                advanceOne(player);
        } else {
            for (int i = days; i < 0; i++)
                retreatOne(player);
        }
        Event event = new MovePlayerEvent(player.getNickname(), days);
        support.firePropertyChange("movePlayer", 0, event);
        Logger.debug("evento movePlayer lanciato");
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
                    //player2 is doubled and so removed from scoreBoard
                    leavePlayer(player2.get());
                    support.firePropertyChange("doubled", player2.get().getNickname(), player);
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
                    leavePlayer(player2.get());
                    support.firePropertyChange("doubled", player2.get().getNickname(), player);
                }
            }
        }
        while (circuit.get(index).isPresent());

        circuit.set(start, Optional.empty());
        circuit.set(index, Optional.of(player));
    }

    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }

    /**
     * Method that gives rewards to players who finished the flight
     */
    public void assignCreditsForPositions(){
        int i = 0;
        int[] vettore = {4, 3, 2, 1};
        for(Player p : scoreBoard){
            p.addCredits(vettore[i]);
            i++;
        }
    }

    /**
     * Method that assign credits to all players
     */
    public void assignCreditsForRemainingGoods(){
        int total = 0;
        for( Player p : players){
            for(GoodType type : GoodType.values()){
                int num = p.getShipBoard().getStoredQuantity(type);
                total = total +num*priceGoods.get(type);
            }
            //if the players didn't finish the flight than the goods are sold for half price
            if(p.getRetired()){
                total = (int) Math.ceil(total/2);
            }
            p.addCredits(total);
        }
    }

    /**
     * Method that assigns 2 credits for the ship with less exposedConnectors
     */
    public void assignCreditsForBeautifulShip() {
        List<Player> winners = new ArrayList<>();
        int minExposed = Integer.MAX_VALUE;

        for (Player p : scoreBoard) {
            int exposed = p.getShipBoard().getExposedConnectors();

            if (exposed < minExposed) {
                winners.clear();
                winners.add(p);
                minExposed = exposed;
            } else if (exposed == minExposed) {
                winners.add(p);
            }
        }

        for (Player p : winners) {
            p.addCredits(2);
        }
    }

    /**
     +     * Method that decrement one credit for every lost component
     +     */
    public void penaltyForDiscardedComponents() {
        for ( Player p : scoreBoard){
            p.addCredits(-(p.getShipBoard().getDiscaredComponents()));
        }
    }






    /**
     *
     * @return the list of all the components loaded by JSON file
     */
    private Map<Integer, Component> loadComponentMap() {
        List<Component> loadedComponents = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(getClass().getResourceAsStream("/components.json"));

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

    /**
     *
     * @return the list of all the SldAdv Cards based on the {@link GameMode} of the game
     */
    protected abstract Map<Integer, SldAdvCard> loadSldAdvCard();

    /**
     *
     * @return the created circuite base on the {@link GameMode} of the game
     */
    protected abstract List<Optional<Player>> createCircuite();

    public void setScoreboard(List<Player> players) {
        this.scoreBoard = players;
    }

    public List<Player> getPlayers(){
        return players;
    }

    public GameMode getMode(){
        return mode;
    }

    public List<Integer> getAvailableDecks(){
        return availableDecks;
    }

    public List<Integer> getAdvDeckByIndex(int index){
        if(index < 0 || index >= littleDecks.size())
            throw new BadParameterException("Index out of decks list bound");
        return new ArrayList<>(littleDecks.get(index));
    }

    public SldAdvCard getPlayedCard(){
        return playedCard;
    }

    public void setPlayedCard(SldAdvCard card){
        this.playedCard = card;
    }

    public int drawCard(){
        this.playedFirstCard = true;

        if(deck.isEmpty())
            return 999;
        int cardId = deck.getFirst();
        deck.removeFirst();
        return cardId;
    }


    public void takeCostructedShip(Player player) throws IncorrectFlyBoardException{
            HousingColor color = player.getColor();

            switch (color) {
                case GREEN -> player.setShipBoard(ShipBoardNormal.buildFirst(this));
                case RED -> player.setShipBoard(ShipBoardNormal.buildRed(this));
                case BLUE -> player.setShipBoard(ShipBoardNormal.buildBlue(this));
                case YELLOW -> player.setShipBoard(ShipBoardNormal.buildYellow(this));
            }
    }


    public abstract ShipBoard getBuiltShip(HousingColor color);

    public void refreshWaitingPlayers(){
        waitingPlayers = new ArrayList<>(scoreBoard);
    }

    public List<Player> getWaitingPlayers(){
        return waitingPlayers;
    }

    public void setValidationPlayers(List<Player> players){
        this.validationPlayers = new ArrayList<>(players);
    }

    public List<Player> getValidationPlayers(){
        return validationPlayers;
    }

    public void leavePlayer(Player player){
        for (int i = 0; i < circuit.size(); i++){
            Optional<Player> optPlayer = circuit.get(i);

            if (optPlayer.isEmpty())
                continue;

            if (optPlayer.get().getNickname().equals(player.getNickname())){
                circuit.set(i, Optional.empty());
                scoreBoard.remove(player);

                Event event = new LeavePlayerEvent(player.getNickname());
                support.firePropertyChange("leavePlayer", null, event);

                return;
            }
        }
    }

    public void setAddCrewPlayers(List<Player> players){
        this.validationPlayers = new ArrayList<>(players);
    }

    public List<Player> getAddCrewPlayers(){
        return validationPlayers;
    }

    public boolean isPlayedFirstCard() {
        return playedFirstCard;
    }

    public void setPlayedFirstCard(boolean playedFirstCard) {
        this.playedFirstCard = playedFirstCard;
    }
}
