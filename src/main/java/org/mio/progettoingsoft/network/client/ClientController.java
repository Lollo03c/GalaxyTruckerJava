package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.advCards.sealed.SldStardust;
import org.mio.progettoingsoft.components.Housing;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.rmi.RmiClient;
import org.mio.progettoingsoft.network.client.socket.SocketClient;
import org.mio.progettoingsoft.network.server.VirtualServer;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientController {
    private static ClientController instance;
    private Client client;
    private int tempIdClient;
    private VirtualServer server;
    private final ConnectionInfo connectionInfo;

    private ClientController(ConnectionInfo connectionInfo) {
        this.setState(GameState.START);
        this.connectionInfo = connectionInfo;
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
    FlyBoard flyBoard;
    ShipBoard shipBoard;

    private String nickname;
    private int idGame;

    private int inHandComponent;
    private int inHandDeck;
    private int tmpRotation;

    private SldAdvCard card;

    private List<Integer> availablePlacesOnCircuit;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void setGameId(int gameId) {
        this.idGame = gameId;
    }

    public void setState(GameState state) {
        GameState oldState;
        synchronized (stateLock) {
            oldState = this.gameState;
            this.gameState = state;
        }
        if (oldState != state) {
            support.firePropertyChange("gameState", oldState, state);
        }
    }

    public GameState getState() {
        synchronized (stateLock) {
            return gameState;
        }
    }

    public void setCardState(CardState state) {
        CardState oldState;
        if(this.getState() != GameState.CARD_EFFECT)
            setState(GameState.CARD_EFFECT);
        synchronized (cardStateLock) {
            oldState = this.cardState;
            this.cardState = state;
        }
        if (oldState != state) {
            support.firePropertyChange("cardState", oldState, state);
            Logger.debug("CARD: " + oldState + " -> " + state);
        }
    }

    public CardState getCardState() {
        synchronized (cardStateLock) {
            return cardState;
        }
    }


    public void applyStardust(SldStardust card) {
        try {
            server.applyStardust(idGame, nickname, card);
        } catch (Exception e) {
            e.printStackTrace();
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

    public void setCardState() {
        //todo da settare gli stati a tutti i giocatori: switch in base alla carta uscita
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

    public void advancePlayer(String nickname, int steps, int energyToRemove) {
        int oldPos, newPos;
        synchronized (flyboardLock) {
            if (this.nickname.equals(nickname)) {
                shipBoard.removeEnergy(energyToRemove);
            }
            Player player = flyBoard.getPlayerByUsername(nickname);
            oldPos = flyBoard.getPlayerPositionOnCircuit(nickname);
            flyBoard.moveDays(player, steps);
            newPos = flyBoard.getPlayerPositionOnCircuit(nickname);
        }
        support.firePropertyChange("circuit", oldPos, newPos);
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
            try {
                server.endBuild(idGame, nickname);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (chosen == 7) {
            setState(GameState.CHOICE_BUILT);
        }
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
        HousingColor color = flyBoard.getPlayerByUsername(nick).getColor();

        try {
            if (nick.equals(nickname)) {
                flyBoard.getPlayerByUsername(nick).setShipBoard(flyBoard.getBuiltShip(color));
                shipBoard = flyBoard.getPlayerByUsername(nick).getShipBoard();

                Logger.debug(nick + " " + color + "assegnato");
                setState(GameState.BUILDING_SHIP);
            }
            else {
                //il ramo di else serve per non creare bug
                Logger.debug(nick + " " + color);
                flyBoard.getPlayerByUsername(nick).setShipBoard(flyBoard.getBuiltShip(color));
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    public void leaveFlight() {
        try {
            server.leaveFlight(idGame, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addCredits(String nick, int credits) {
        Logger.info(nick + " added " + credits + "credits");
        synchronized (flyboardLock) {
            flyBoard.getPlayerByUsername(nick).addCredits(credits);
        }
    }

    public void crewLost(String nick, List<Cordinate> housingCordinates) {
        ShipBoard ship = flyBoard.getPlayerByUsername(nick).getShipBoard();

        for (Cordinate cord : housingCordinates) {
            Logger.info(nick + "lost crew member in " + cord);
            ship.getOptComponentByCord(cord).get().removeGuest();
        }
    }

    public void skipEffect(){
        try{
            server.skipEffect(idGame, nickname, card.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeCrew(List<Cordinate> cordinatesToRemove){
        try {
            server.crewRemove(idGame, nickname, cordinatesToRemove);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
