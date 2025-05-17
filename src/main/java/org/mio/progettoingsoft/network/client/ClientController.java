package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.view.ShipCell;
import org.mio.progettoingsoft.view.VisualShipboard;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientController {
    private static ClientController instance;
    private Client client;

    private ClientController(){
        stateQueue.add(GameState.START);
    }

    public static synchronized ClientController getInstance(){
        if (instance == null)
            instance = new ClientController();

        return instance;
    }

    private GameState gameState = GameState.START;
    private final Object stateLock = new Object();
    private final Object flyboardLock = new Object();

    BlockingQueue<GameState> stateQueue = new LinkedBlockingQueue<>();
    FlyBoard flyBoard;
    ShipBoard shipBoard;

    private String nickname;
    private int idGame;

    private int inHandComponent;

    public void setGameId(int gameId){
        this.idGame = gameId;
    }

    public void setState(GameState state){
        stateQueue.add(state);
    }

    public GameState getState(){
        synchronized (stateLock){
            return gameState;
        }
    }

    public Object getStateLock(){
        return stateLock;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void connectToServer(boolean isRmi){
        try {
            client = isRmi ? new ClientRmi() : new ClientSocket();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.connect();


        setState(GameState.NICKNAME);
    }

    public BlockingQueue<GameState> getStateQueue() {
        return stateQueue;
    }

    public void handleNickname(String nickname){
        client.handleNickname(nickname);
    }

    public void setGameInfo(GameInfo gameInfo){
        client.handleGameInfo(gameInfo);
    }

    public FlyBoard getFlyBoard(){
        return flyBoard;
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
            inHandComponent = client.getCoveredComponent(idGame);
            setState(GameState.ADD_COMPONENT);
        }
        else if (chosen == 2){

        }
        else if (chosen == 3){
            setState(GameState.VIEW_SHIP_BUILDING);
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
}
