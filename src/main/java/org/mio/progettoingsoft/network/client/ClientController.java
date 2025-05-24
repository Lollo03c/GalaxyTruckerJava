package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.*;
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
import java.util.Map;

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

    public static void create(ConnectionInfo connectionInfo){
        if(instance == null){
            instance = new ClientController(connectionInfo);
        }else{
            throw new RuntimeException("Client controller already exists");
        }
    }

    public static synchronized ClientController getInstance() {
        if (instance == null)
            throw new RuntimeException("Client controller not created");
        return instance;
    }

    private GameState gameState;
    private final Object stateLock = new Object();
    private final Object flyboardLock = new Object();
    FlyBoard flyBoard;
    ShipBoard shipBoard;

    private String nickname;
    private int idGame;

    private int inHandComponent;
    private int inHandDeck;
    private int tmpRotation;

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
            Logger.debug("State changed from " + oldState + " to " + state);
        }
    }

    public GameState getState() {
        synchronized (stateLock) {
            return gameState;
        }
    }

    public String getNickname() {
        return nickname;
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
        return flyBoard;
    }

    public GameInfo getGameInfo() {
        return new GameInfo(idGame, flyBoard.getMode(), flyBoard.getNumPlayers());
    }

    public Object getFlyboardLock() {
        return flyboardLock;
    }

    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players) {
        synchronized (flyboardLock) {
            flyBoard = FlyBoard.createFlyBoard(mode, players.keySet());
            for (Player player : flyBoard.getPlayers()) {
                HousingColor color = players.get(player.getNickname());
                player.setHousingColor(color);
            }
            shipBoard = flyBoard.getPlayerByUsername(nickname).getShipBoard();
        }
    }

    public int getInHandComponent() {
        return inHandComponent;
    }

    public Component getInHandComponentObject() {
        return flyBoard.getComponentById(inHandComponent);
    }

    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    public void increaseTmpRotation() {
        if(tmpRotation < 3){
            tmpRotation++;
        }else {
            tmpRotation = 0;
        }
    }

    public void resetTmpRotation(){
        tmpRotation = 0;

    }

    public int getTmpRotation() {
        return tmpRotation;
    }

    public void addComponent(Cordinate cordinate, int rotations) {
        try {
            shipBoard.addComponentToPosition(inHandComponent, cordinate, rotations);

            server.addComponent(idGame, nickname, inHandComponent, cordinate, rotations);
            inHandComponent = -1;

            setState(GameState.BUILDING_SHIP);
        } catch (IncorrectShipBoardException e) {
            setState(GameState.ERROR_PLACEMENT);
        } catch (Exception e) {
            shipBoard.removeComponent(cordinate);
            throw new RuntimeException(e);
        }
    }

    public void addOtherComponent(String nickname, int idComp, Cordinate cordinate, int rotations) {
        ShipBoard otherShipboard = flyBoard.getPlayerByUsername(nickname).getShipBoard();

        synchronized (otherShipboard) {
            otherShipboard.addComponentToPosition(idComp, cordinate, rotations);
        }
    }

    public void setInHandComponent(int idComp) {
        this.inHandComponent = idComp;
    }

    public void discardComponent() {
        try {
            server.discardComponent(idGame, inHandComponent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setState(GameState.BUILDING_SHIP);
    }

    public int getIdGame() {
        return idGame;
    }

    public void addUncoveredComponent(int idComp) {
        synchronized (flyBoard.getUncoveredComponents()) {
            flyBoard.getUncoveredComponents().add(idComp);
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

    public void removeUncovered(Integer idComp) {
        synchronized (flyBoard.getUncoveredComponents()) {
            flyBoard.getUncoveredComponents().remove(idComp);
        }
    }

    public void bookDeck(Integer deckNumber) {
        try {
            server.bookDeck(idGame, nickname, deckNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeDeck(Integer deckNumber) {
        synchronized (flyBoard.getAvailableDecks()) {
            flyBoard.getAvailableDecks().remove(deckNumber);
        }
    }

    public void setInHandDeck(int deckNumber) {
        inHandDeck = deckNumber;
    }

    public int getInHandDeck() {
        return inHandDeck;
    }

    public void freeDeck() {
        try {
            server.freeDeck(idGame, nickname, inHandDeck);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addAvailableDeck(int deckNumber) {
        synchronized (flyBoard.getAvailableDecks()) {
            flyBoard.getAvailableDecks().add(deckNumber);
        }
    }

    public void bookComponent(){
        try {
            flyBoard.getPlayerByUsername(nickname).getShipBoard().addBookedComponent(inHandComponent);
            setState(GameState.BUILDING_SHIP);
        } catch (IncorrectShipBoardException e) {
            setState(GameState.SWITCH_BOOKED);
        }
    }

    public void bookComponent(int posToRemove){
        int idComp = shipBoard.getBookedComponents().get(posToRemove).get();

        shipBoard.swapBookComponent(inHandComponent, posToRemove);
        inHandComponent = idComp;
        setState(GameState.COMPONENT_MENU);
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
        if (chosen == 1) {
            try {
                server.getCoveredComponent(idGame, nickname);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (chosen == 2) {
            setState(GameState.DRAW_UNCOVERED_COMPONENTS);
        } else if (chosen == 3) {
            setState(GameState.VIEW_SHIP_BUILDING);
        }
        else if (chosen == 4 && flyBoard.getMode().equals(GameMode.NORMAL)) {
            setState(GameState.VIEW_DECKS_LIST);
        }
        else if (chosen == 4 && flyBoard.getMode().equals(GameMode.EASY)){
            //server.playerReady()
            setState(GameState.END_BUILDING);

        }
    }
}
