package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.Map;

public class ClientController {
    private static ClientController instance;
    private Client client;

    private ClientController(){
        this.setState(GameState.START);
    }

    public static synchronized ClientController getInstance(){
        if (instance == null)
            instance = new ClientController();

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

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener){
        support.addPropertyChangeListener(listener);
    }

    public void setGameId(int gameId){
        this.idGame = gameId;
    }

    public void setState(GameState state){
        GameState oldState;
        synchronized (stateLock) {
            oldState = this.gameState;
            this.gameState = state;
        }
        if(oldState != state){
            support.firePropertyChange("gameState", oldState, state);
            System.out.println(oldState + " -> " + state);
        }
    }

    public GameState getState(){
        synchronized (stateLock){
            return gameState;
        }
    }

    public String getNickname(){
        return nickname;
    }

    public Object getStateLock(){
        return stateLock;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void connectToServer(boolean isRmi){
        setState(GameState.WAITING);
        try {
            client = isRmi ? new ClientRmi() : new ClientSocket();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.connect();
        setState(GameState.NICKNAME);
        synchronized (this){
            this.notifyAll();
        }
    }

    public void handleNickname(String nickname){
        client.handleNickname(nickname);
    }

    public void setGameInfo(GameInfo gameInfo){
        client.handleGameInfo(gameInfo, nickname);
    }

    public FlyBoard getFlyBoard(){
        return flyBoard;
    }

    public GameInfo getGameInfo(){
        return new GameInfo(idGame, flyBoard.getMode(), flyBoard.getNumPlayers());
    }

    public Object getFlyboardLock(){
        return flyboardLock;
    }

    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players){
        synchronized (flyboardLock){
            flyBoard = FlyBoard.createFlyBoard(mode, players.keySet());


            for (Player player : flyBoard.getPlayers()) {
                HousingColor color = players.get(player.getNickname());
                player.setHousingColor(color);
            }
            shipBoard = flyBoard.getPlayerByUsername(nickname).getShipBoard();
        }
    }

    public void handleBuildingShip(int chosen){
        if (chosen == 1){
            client.getCoveredComponent(idGame);
        }
        else if (chosen == 2){
            setState(GameState.DRAW_UNCOVERED_COMPONENTS);
        }
        else if (chosen == 3){
            setState(GameState.VIEW_SHIP_BUILDING);
        }
        else if (chosen == 4){
            setState(GameState.VIEW_DECKS_LIST);
        }
    }

    public int getInHandComponent(){
        return inHandComponent;
    }

    public ShipBoard getShipBoard(){
        return shipBoard;
    }

    public void addComponent(Cordinate cordinate, int rotations){

        try {
            shipBoard.addComponentToPosition(inHandComponent, cordinate, rotations);

            client.handleComponent(idGame, nickname, inHandComponent, cordinate, rotations);
            inHandComponent = -1;

            setState(GameState.BUILDING_SHIP);
        } catch (IncorrectShipBoardException e) {
            setState(GameState.ERROR_PLACEMENT);
        }
    }

    public void addOtherComponent(String nickname, int idComp, Cordinate cordinate, int rotations){
        ShipBoard otherShipboard = flyBoard.getPlayerByUsername(nickname).getShipBoard();

        synchronized (otherShipboard){
            otherShipboard.addComponentToPosition(idComp, cordinate, rotations);
        }
    }

    public void setInHandComponent(int idComp){
        this.inHandComponent = idComp;
    }

    public void discardComponent(){
        client.discardComponent(inHandComponent);
        setState(GameState.BUILDING_SHIP);
    }

    public int getIdGame(){
        return idGame;
    }

    public void addUncoveredComponent(int idComp){
        synchronized (flyBoard.getUncoveredComponents()) {
            flyBoard.getUncoveredComponents().add(idComp);
        }
    }

    public void drawCovered(int idComp){
        client.drawUncovered(idComp);
    }

    public void removeUncovered(Integer idComp){
        synchronized (flyBoard.getUncoveredComponents()){
            flyBoard.getUncoveredComponents().remove(idComp);
        }
    }

    public void bookDeck(Integer deckNumber){
        client.bookDeck(deckNumber);
    }

    public void removeDeck(Integer deckNumber){
        synchronized (flyBoard.getAvailableDecks()) {
            flyBoard.getAvailableDecks().remove(deckNumber);
        }
    }

    public void setInHandDeck(int deckNumber){
        inHandDeck = deckNumber;
    }

    public int getInHandDeck(){
        return inHandDeck;
    }

    public void freeDeck(){
        client.freeDeck(inHandDeck);
    }

    public void addAvailableDeck(int deckNumber){
        synchronized (flyBoard.getAvailableDecks()){
            flyBoard.getAvailableDecks().add(deckNumber);
        }
    }
}
