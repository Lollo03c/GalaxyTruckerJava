package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.CannotRotateHourglassException;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.network.client.rmi.RmiClient;
import org.mio.progettoingsoft.network.client.socket.SocketClient;
import org.mio.progettoingsoft.network.server.VirtualServer;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.stream.Collectors;

public class ClientController {
    private static ClientController instance;
    private Client client;
    private int tempIdClient;
    private VirtualServer server;
    private final ConnectionInfo connectionInfo;
    private int hourglassCounter = 0;
    private boolean pendingHourglass = true;
    private boolean finishedBuilding = false;
    private Boolean finishedLastHourglass = false;

    public boolean getFinishedBuilding() {
        return finishedBuilding;
    }

    public int getHourglassCounter() {
        return hourglassCounter;
    }

    public void setPendingHourglass(boolean pendingHourglass) {
        this.pendingHourglass = pendingHourglass;
    }

    public void incrementHourglassCounter() {
        hourglassCounter++;
        support.firePropertyChange("hourglassCounter", null, hourglassCounter);
    }


    private ClientController(ConnectionInfo connectionInfo) {
        this.setState(GameState.START);
        this.connectionInfo = connectionInfo;
    }

    public boolean getPendingHourglass() {
        return pendingHourglass;
    }

    public static void create(ConnectionInfo connectionInfo) {
        if (instance == null) {
            instance = new ClientController(connectionInfo);
        } else {
            throw new RuntimeException("Client controller already exists");
        }
    }

    public static synchronized ClientController getInstance() {
        if (instance == null)
            throw new RuntimeException("Client controller not created");
        return instance;
    }

    private GameState gameState;
    private CardState cardState;
    private final Object stateLock = new Object();
    private final Object flyboardLock = new Object();
    private final Object shipboardLock = new Object();
    private final Object listLock = new Object();
    private final Object cardLock = new Object();
    private final Object cardStateLock = new Object();
    private final Object hourglassLock = new Object();
    FlyBoard flyBoard;
    ShipBoard shipBoard;

    private String nickname;
    private int idGame;

    private int inHandComponent;
    private int inHandDeck;
    private int tmpRotation;

    private SldAdvCard card;

    private List<Integer> availablePlacesOnCircuit;

    private List<GoodType> goodsToInsert = new ArrayList<>();
    private Meteor meteor;
    private CannonPenalty cannon;

    private String choiceErrorMessage;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void setFinishedLastHourglass(boolean finishedLastHourglass) {
        synchronized (hourglassLock) {
            this.finishedLastHourglass = finishedLastHourglass;
        }
    }

    public Boolean getFinishedLastHourglass() {
        synchronized (hourglassLock) {
            return this.finishedLastHourglass;
        }
    }

    public void setGameId(int gameId) {
        this.idGame = gameId;
    }

    public void setState(GameState state) {
        GameState oldState;
        if (state.equals(GameState.FINISH_HOURGLASS))
            pendingHourglass = false;
        synchronized (hourglassLock) {
            if (state.equals(GameState.FINISH_LAST_HOURGLASS)) {
                finishedLastHourglass = true;
                pendingHourglass = false;
            }
        }
        synchronized (stateLock) {
            oldState = this.gameState;
            this.gameState = state;
        }

        if (oldState == null || !oldState.equals(state)) {
            support.firePropertyChange("gameState", oldState, state);

        } else {
            support.firePropertyChange("gameState", oldState, GameState.IDLE);
            support.firePropertyChange("gameState", GameState.IDLE, state);
        }
        Logger.debug("GameState: " + oldState + " -> " + state);
    }

    public GameState getState() {
        synchronized (stateLock) {
            return gameState;
        }
    }

    public void setCardState(CardState state) {
        CardState oldState;
        synchronized (cardStateLock) {
            oldState = this.cardState;
            this.cardState = state;
        }
        synchronized (stateLock) {
            setState(GameState.CARD_EFFECT);
        }
        if (oldState != state) {
            support.firePropertyChange("cardState", oldState, state);
            Logger.debug("CardState: " + oldState + " -> " + state);
        } else {
            support.firePropertyChange("cardState", oldState, CardState.IDLE);
            support.firePropertyChange("cardState", CardState.IDLE, state);
            Logger.debug("CardState: " + oldState + " -> " + state);
        }
    }

    public CardState getCardState() {
        synchronized (cardStateLock) {
            return cardState;
        }
    }

    public String getNickname() {
        return nickname;
    }

    public List<Integer> getAvailablePlacesOnCircuit() {
        synchronized (listLock) {
            return new ArrayList<>(availablePlacesOnCircuit);
        }
    }

    public Object getStateLock() {
        return stateLock;
    }

    public void setIdClient(int idClient) {
        this.tempIdClient = idClient;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public FlyBoard getFlyBoard() {
        synchronized (flyboardLock) {
            return flyBoard;
        }
    }

    public GameInfo getGameInfo() {
        return new GameInfo(idGame, flyBoard.getMode(), flyBoard.getNumPlayers());
    }

    public Object getFlyboardLock() {
        return flyboardLock;
    }

    public Object getShipboardLock() {
        return shipboardLock;
    }

    public int getInHandComponent() {
        return inHandComponent;
    }

    public Component getInHandComponentObject() {
        return flyBoard.getComponentById(inHandComponent);
    }

    public ShipBoard getShipBoard() {
        synchronized (shipboardLock) {
            return shipBoard;
        }
    }

    public Meteor getMeteor() {
        return meteor;
    }

    public void increaseTmpRotation() {
        if (tmpRotation < 3) {
            tmpRotation++;
        } else {
            tmpRotation = 0;
        }
    }

    public void resetTmpRotation() {
        tmpRotation = 0;

    }

    public int getTmpRotation() {
        return tmpRotation;
    }

    public void setInHandComponent(int idComp) {
        this.inHandComponent = idComp;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setInHandDeck(int deckNumber) {
        inHandDeck = deckNumber;
    }

    public int getInHandDeck() {
        return inHandDeck;
    }

    public List<Optional<Player>> getCircuit() {
        synchronized (flyboardLock) {
            return flyBoard.getCircuit();
        }
    }

    public String getErrMessage() {
        return choiceErrorMessage;
    }

    public void resetErrMessage() {
        choiceErrorMessage = null;
    }

    /*
     * methods called by the server to update the game state (and the model)
     */

    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players, List<List<Integer>> decks) {
        synchronized (flyboardLock) {
            flyBoard = FlyBoard.createFlyBoard(mode, players.keySet());
            for (Player player : flyBoard.getPlayers()) {
                HousingColor color = players.get(player.getNickname());
                player.setHousingColor(color);
            }
            shipBoard = flyBoard.getPlayerByUsername(nickname).getShipBoard();
            if (mode == GameMode.NORMAL) {
                flyBoard.setLittleDecks(decks);
            }
        }
    }

    public void setCard(int idCard) {
        synchronized (cardLock) {
            synchronized (flyboardLock) {
                this.card = flyBoard.getSldAdvCardByID(idCard);
                goodsToInsert = new ArrayList<>(card.getGoods());
                Logger.debug("settata la carta: " + card.getCardName());
            }
        }
    }

    public SldAdvCard getPlayedCard() {
        synchronized (cardLock) {
            return card;
        }
    }

    public void addUncoveredComponent(int idComp) {
        synchronized (flyBoard.getUncoveredComponents()) {
            flyBoard.getUncoveredComponents().add(idComp);
        }
    }

    public void removeUncovered(Integer idComp) {
        synchronized (flyBoard.getUncoveredComponents()) {
            flyBoard.getUncoveredComponents().remove(idComp);
        }
    }

    public void addOtherPlayersComponent(String nickname, int idComp, Cordinate cordinate, int rotations) {
        ShipBoard otherShipboard = flyBoard.getPlayerByUsername(nickname).getShipBoard();

        synchronized (otherShipboard) {
            otherShipboard.addComponentToPosition(idComp, cordinate, rotations);
        }
    }

    public void updateState() {

    }

    public void addAvailableDeck(int deckNumber) {
        synchronized (flyBoard.getAvailableDecks()) {
            flyBoard.getAvailableDecks().add(deckNumber);
        }
    }

    public void removeDeck(Integer deckNumber) {
        synchronized (flyBoard.getAvailableDecks()) {
            flyBoard.getAvailableDecks().remove(deckNumber);
        }
    }

    public void setAvailablePlaces(List<Integer> availablePlaces) {
        synchronized (listLock) {
            this.availablePlacesOnCircuit = new ArrayList<>(availablePlaces);
        }
    }

    public void addOtherPlayerToCircuit(String nickname, int place) {
        synchronized (flyboardLock) {
            flyBoard.addPlayerToCircuit(nickname, place);
        }
    }

    public void advancePlayer(String nickname, int steps) {
        int oldPos, newPos;
        synchronized (flyboardLock) {
            Player player = flyBoard.getPlayerByUsername(nickname);
            oldPos = flyBoard.getPlayerPositionOnCircuit(nickname);
            flyBoard.moveDays(player, steps);
            newPos = flyBoard.getPlayerPositionOnCircuit(nickname);
        }
        support.firePropertyChange("circuit", oldPos, newPos);
        Logger.debug("Moved " + nickname + " from " + oldPos + " to " + newPos);
    }

    public void genericChoiceError(String msg) {
        CardState old = getCardState();
        choiceErrorMessage = msg;
        setCardState(CardState.ERROR_CHOICE);
        setCardState(old);
    }

    /*
     * Methods called by the view to handle the input and communicate with the server
     */

    public void connectToServer(boolean isRmi) {
        setState(GameState.WAITING);
        try {
            client = isRmi ? new RmiClient(connectionInfo) : new SocketClient(connectionInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            client.connect();
            server = client.getServer();
            setState(GameState.NICKNAME);
            synchronized (this) {
                this.notifyAll();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startHourglass() {
        try {
            pendingHourglass = true;
            server.startHourglass(idGame);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleNickname(String nickname) {
        try {
            server.handleNickname(tempIdClient, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleGameInfo(GameInfo gameInfo) {
        try {
            server.handleGameInfo(gameInfo, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleBuildingShip(int chosen) {
        System.out.println("choice : " + chosen);
        if (chosen == 1) {
            try {
                server.getCoveredComponent(idGame, nickname);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (chosen == 2) {
            setState(GameState.DRAW_UNCOVERED_COMPONENTS);
        } else if (chosen == 3) {
            setState(GameState.VIEW_BOOKED);
        } else if (chosen == 4) {
            setState(GameState.VIEW_SHIP_BUILDING);
        } else if (chosen == 5 && flyBoard.getMode().equals(GameMode.NORMAL)) {
            setState(GameState.VIEW_DECKS_LIST);
        } else if (chosen == 5 && flyBoard.getMode().equals(GameMode.EASY)) {
            //server.playerReady()
            setState(GameState.END_BUILDING);
        } else if (chosen == 6) {
            setState(GameState.END_BUILDING);

            try {
                finishedBuilding = true;
                server.endBuild(idGame, nickname);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (chosen == 7) {
            setState(GameState.CHOICE_BUILT);
        } else if (chosen == 8) {
            try {
                if (pendingHourglass) {
                    throw new CannotRotateHourglassException("hourglass timer is already started");
                } else if (hourglassCounter == 2 && !getState().equals(GameState.END_BUILDING)) {
                    throw new CannotRotateHourglassException("you cannot rotate hourglass : you need to finish your ship building first");
                } else if (hourglassCounter == 3) {
                    throw new CannotRotateHourglassException("hourglass cannote be rotated anymore");
                }
                pendingHourglass = true;
                server.startHourglass(idGame);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            setState(GameState.BUILDING_SHIP);
        }
    }

    // this method is called only by the players who have already finished the ship building
    public void rotateHourglass() {
        try {
            if (pendingHourglass) {
                throw new CannotRotateHourglassException("hourglass timer is already started");
            } else if (hourglassCounter == 3) {
                throw new CannotRotateHourglassException("hourglass cannote be rotated anymore");
            }
            pendingHourglass = true;
            server.startHourglass(idGame);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setState(GameState.END_BUILDING);
    }

    public void drawUncovered(int idComp) {
        if (!flyBoard.getUncoveredComponents().contains(idComp)) {
            setState(GameState.UNABLE_UNCOVERED_COMPONENT);
            return;
        }
        try {
            server.drawUncovered(idGame, nickname, idComp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void landOnPlanet(int choice) {
        try {
            server.landOnPlanet(idGame, nickname, choice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void addComponent(Cordinate cordinate, int rotations) {
        try {
            shipBoard.addComponentToPosition(inHandComponent, cordinate, rotations);

            server.addComponent(idGame, nickname, inHandComponent, cordinate, rotations);
            inHandComponent = -1;
            resetTmpRotation();

            setState(GameState.BUILDING_SHIP);
        } catch (IncorrectShipBoardException e) {
            setState(GameState.ERROR_PLACEMENT);
        } catch (Exception e) {
            shipBoard.removeComponent(cordinate);
            throw new RuntimeException(e);
        }
    }

    public void bookComponent() {
        try {
            flyBoard.getPlayerByUsername(nickname).getShipBoard().addBookedComponent(inHandComponent);
            setState(GameState.BUILDING_SHIP);
        } catch (IncorrectShipBoardException e) {
            setState(GameState.SWITCH_BOOKED);
        }
    }

    public void choseBookedComponent(int pos) {
        int idComp = shipBoard.getBookedComponents().get(pos - 1).get();
        shipBoard.removedBookedComponent(pos - 1);

        inHandComponent = idComp;
        setState(GameState.COMPONENT_MENU);
    }

    public void bookComponent(int posToRemove) {
        int idComp = shipBoard.getBookedComponents().get(posToRemove).get();

        shipBoard.swapBookComponent(inHandComponent, posToRemove);
        inHandComponent = idComp;
        setState(GameState.COMPONENT_MENU);
    }

    public void discardComponent() {
        Component comp = flyBoard.getComponentById(inHandComponent);
        comp.reinitilizeRotations();

        try {
            server.discardComponent(idGame, inHandComponent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setState(GameState.BUILDING_SHIP);
    }

    public void getBooked(int index) {
        int ret = -1;
        if (index == 0 || index == 1) {
            synchronized (shipboardLock) {
                if (shipBoard.getBookedComponents().get(index).isPresent()) {
                    inHandComponent = shipBoard.getBookedComponents().get(index).get();
                    if (index == 0) {
                        shipBoard.getBookedComponents().remove(index);
                        shipBoard.getBookedComponents().add(Optional.empty());
                    } else {
                        shipBoard.getBookedComponents().set(index, Optional.empty());
                    }

                    setState(GameState.COMPONENT_MENU);
                }
            }
        }
    }

    public void bookDeck(Integer deckNumber) {
        try {
            server.bookDeck(idGame, nickname, deckNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void freeDeck() {
        try {
            server.freeDeck(idGame, nickname, inHandDeck);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void builtDefault() {
        try {
            server.takeBuild(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void assignBuild(String nick) {
        synchronized (shipboardLock) {
            HousingColor color = flyBoard.getPlayerByUsername(nick).getColor();

            try {
                if (nick.equals(nickname)) {
                    flyBoard.getPlayerByUsername(nick).setShipBoard(flyBoard.getBuiltShip(color));
                    shipBoard = flyBoard.getPlayerByUsername(nick).getShipBoard();

                    Logger.debug(nick + " " + color + "assegnato");
                } else {
                    //il ramo di else serve per non creare bug
                    Logger.debug(nick + " " + color);
                    flyBoard.getPlayerByUsername(nick).setShipBoard(flyBoard.getBuiltShip(color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Cordinate> getIncorrectComponents() {
        return shipBoard.getIncorrectComponents();
    }

    public List<Set<Component>> getStandAloneBlocks() {
        return shipBoard.getMultiplePieces();
    }

    public void removeStandAloneBlocks(int blockToKeep) {
        List<Set<Component>> standAloneBlocks = shipBoard.getMultiplePieces();

        List<Cordinate> componentsToRemove;
        for (int i = 0; i < standAloneBlocks.size(); i++) {
            if (i != blockToKeep) {
                componentsToRemove = standAloneBlocks.get(i).stream().map(Component::getCordinate).toList();
                removeComponents(componentsToRemove);
            }
        }
    }

    public void endValidation() {
        try {
            server.endValidation(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void endBuild() {
        try {
            server.endBuild(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void choosePlace(int place) {
        try {
            server.choosePlace(idGame, nickname, place);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void drawNewAdvCard() {
        try {
            server.drawCard(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void activateDoubleEngine(int number) {
        try {
            server.activateDoubleEngine(idGame, nickname, number);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<GoodType> getGoodsToInsert() {
        synchronized (listLock) {
            return new ArrayList<>(goodsToInsert);
        }
    }

    public List<GoodType> getPlanetGoods() {
        Player player;
        List<GoodType> toInsert = new ArrayList<>();
        synchronized (flyboardLock) {
            player = flyBoard.getPlayerByUsername(nickname);
        }
        synchronized (cardLock) {
            toInsert = card.getPlanets().stream()
                    .filter(p -> p.getPlayer().isPresent() && p.getPlayer().get().equals(player))
                    .map(x -> x.getGoods())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
        Logger.debug("getGoodsToInsert: " + toInsert);
        return toInsert;
    }

    public void leaveFlight(boolean leave) {
        try {
            server.leaveFlight(idGame, nickname, leave);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addCredits(String nick, int credits) {
        Logger.info(nick + " added " + credits + "credits");
        int tot;
        synchronized (flyboardLock) {
            flyBoard.getPlayerByUsername(nick).addCredits(credits);
            tot = flyBoard.getPlayerByUsername(nick).getCredits();
        }
        support.firePropertyChange("credits", 0, tot);
    }

    public void crewLost(int idComp) {
        synchronized (flyBoard) {
            Logger.info("lost crew member in " + idComp);
            flyBoard.getComponentById(idComp).removeGuest();
        }
    }

    public void skipEffect() {
        goodsToInsert.clear();
        try {
            server.skipEffect(idGame, nickname, card.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeCrew(List<Cordinate> cordinatesToRemove) {
        try {
            server.crewRemove(idGame, nickname, cordinatesToRemove);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addGood(int idComp, GoodType type) {
        try {
            server.addGood(idGame, nickname, idComp, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addGoodToModel(int idComp, GoodType type) {
        synchronized (flyboardLock) {
            flyBoard.getComponentById(idComp).addGood(type);
        }
    }

    public void removePendingGood(String nick, GoodType type) {
        if (nick.equals(nickname)) {
            synchronized (listLock) {
                goodsToInsert.remove(type);
            }
        }
    }

    public void removeGood(int idComp, GoodType type) {
        Logger.debug("Ask the server for remove " + type + " from " + idComp);
        try {
            server.removeGood(idGame, nickname, idComp, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeGoodFromModel(int idComp, GoodType type) {
        synchronized (flyboardLock) {
            Logger.debug(type + "removed from " + idComp);
            flyBoard.getComponentById(idComp).removeGood(type);
        }
    }

    public void addPendingGood(String nick, GoodType type) {
        if (nick.equals(nickname))
            synchronized (listLock) {
                goodsToInsert.add(type);
            }
        Logger.debug("Added to pending " + nick);
    }

    public void setPlayerOnPlanet(String nickname, int choice) {
        List<GoodType> tmpList = null;
        synchronized (cardLock) {
            Player player = flyBoard.getPlayerByUsername(nickname);
            card.getPlanets().get(choice).land(player);
        }
    }

    public void applyEffect() {
        try {
            server.applyEffect(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void activateDoubleDrills(List<Cordinate> drillsCordinate) {
        try {
            server.activateDoubleDrills(idGame, nickname, drillsCordinate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void activateSlaver(List<Cordinate> activatedDrills, boolean wantsToActivate) {
        try {
            server.activateSlaver(idGame, nickname, activatedDrills, wantsToActivate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setRollResult(int first, int second) {
        try {
            server.setRollResult(idGame, nickname, first, second);
        } catch (Exception e) {
        }
    }


//    public void removeBattery(int quantity){
//        try{
//            server.removeBattery(idGame, nickname, quantity);
//        }
//        catch (Exception e){
//            throw new RuntimeException("");
//        }
//    }

    public void removeBatteriesFromModel(List<Integer> batteryDepotId) {
        synchronized (flyboardLock) {
            for (int id : batteryDepotId) {
                Logger.debug("Removed battery from component id " + id);
                flyBoard.getComponentById(id).removeOneEnergy();
            }
        }
    }

    public void advanceMeteor(boolean destroyed, boolean energy) {
        try {
            server.advanceMeteor(idGame, nickname, destroyed, energy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeComponent(Cordinate cordinate){
        try{
            server.removeComponent(idGame, nickname, cordinate, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeComponentImmediate(Cordinate cordinate){
        try{
            shipBoard.removeComponent(cordinate);
            server.removeComponent(idGame, nickname, cordinate, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeComponents(List<Cordinate> cordinatesToRemove){
        for (Cordinate cordinate : cordinatesToRemove) {
            removeComponentImmediate(cordinate);
        }
    }

    public void removeComponentFromModel(String nickname, Cordinate cord){
        synchronized (flyboardLock) {
            Logger.debug("removed component of " + nickname + " from cordinate " + cord);
            ShipBoard otherShip = flyBoard.getPlayerByUsername(nickname).getShipBoard();
            otherShip.removeComponent(cord);
        }
    }

    public void meteorHit(MeteorType type, Direction direction, int number, Cordinate cord){
        meteor = new Meteor(direction, type);
        meteor.setNumber(number);
        meteor.setCordinateHit(cord);
        setCardState(CardState.METEOR_HIT);

        Logger.info(type + " " + direction + " " + number);
    }

    public void cannonHit(CannonType type, Direction direction, int number) {
        cannon = new CannonPenalty(direction, type);
        cannon.setNumber(number);

        Optional<Cordinate> optCordinateHit = cannon.findHit(shipBoard, number);
        if (optCordinateHit.isEmpty()) {
            advanceCannon(false, false);
            return;
        }

        Cordinate cordinateHit = optCordinateHit.get();
        Component componentHit = shipBoard.getOptComponentByCord(cordinateHit).get();

        cannon.setCordinateHit(cordinateHit);

        if (type.equals(CannonType.HEAVY)) {
            advanceCannon(true, false);
        } else {
            setCardState(CardState.SHIELD_SELECTION);
        }


    }

    public void advanceCannon(boolean destroyed, boolean energy) {
        try {
            server.advanceCannon(idGame, nickname, destroyed, energy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CannonPenalty getCannon() {
        return cannon;
    }

    public void removeBatteryFromModel(int idBatteryDepot) {
        Logger.debug("remove battery from " + idBatteryDepot);
        synchronized (flyBoard) {
            flyBoard.getComponentById(idBatteryDepot).removeOneEnergy();
        }
    }

    public void leaveFlightFromModel(String nickname){
        synchronized (flyboardLock){
            Player player = flyBoard.getPlayerByUsername(nickname);
            flyBoard.getScoreBoard().remove(player);

            for (int i = 0; i < flyBoard.getCircuit().size(); i++){
                Optional<Player> optionalPlayer = flyBoard.getCircuit().get(i);

                if (optionalPlayer.isEmpty())
                    continue;

                if (optionalPlayer.get().getNickname().equals(nickname)){
                    flyBoard.getCircuit().set(i, Optional.empty());
                    return;
                }
            }
        }
    }
}
