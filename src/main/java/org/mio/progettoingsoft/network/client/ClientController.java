package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameClient;
import org.mio.progettoingsoft.model.state.ClientState;
import org.mio.progettoingsoft.model.state.ConnectionSetup;
import org.mio.progettoingsoft.network.ConnectionType;
import org.mio.progettoingsoft.network.GuiController;
import org.mio.progettoingsoft.network.NetworkFactory;
import org.mio.progettoingsoft.network.TuiController;
import org.mio.progettoingsoft.network.input.Input;
import org.mio.progettoingsoft.network.input.IntInput;
import org.mio.progettoingsoft.network.input.SetupInput;
import org.mio.progettoingsoft.network.input.StringInput;
import org.mio.progettoingsoft.network.message.CoveredComponentMessage;
import org.mio.progettoingsoft.network.message.GameSetupMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.NicknameMessage;
import org.mio.progettoingsoft.views.VirtualView;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

public abstract class ClientController implements Runnable {
    /**
     * SINGLETON IMPLEMENTATION
     */
    private static ClientController instance;

    public static ClientController create(boolean isGui) {
        if (instance == null) {
            if (isGui) {
                instance = new GuiController();
            } else {
                instance = new TuiController();
            }
        }
        return instance;
    }

    public static ClientController get() {
        return instance;
    }

    public void setMessageQueue(BlockingQueue<Message> messageQueue) {
        this.inputMessageQueue = messageQueue;
    }

    protected ClientState state = new ConnectionSetup();
    protected ClientState nextState;

    protected GameState gameState = GameState.START;
    protected Client client;
    private BlockingQueue<Message> inputMessageQueue;
    private String nickname;
    private VirtualView view;

    protected final Object stateLock = new Object();
    protected final Object stateLock2 = new Object();

    private int setupClientid;

    private GameClient game;
    private FlyBoard flyBoard;

    public String getNickname(){
        return nickname;
    }

    protected void handleInput(Input input) throws Exception {
        switch (this.getGameState()) {
//            case START -> handleConnectionTypeInput(input);
//            case NICKNAME_REQUEST -> handleNicknameInput(input);
//            case SETUP_GAME -> handleSetupGame(input);
            case PRINT_GAME_INFO -> {
            }
            case WAITING -> {
                synchronized (this) {
                    while (this.getGameState() == GameState.WAITING) {
                        this.wait();
                    }
                }
            }
            case BUILDING_SHIP -> {
                System.out.println("partita iniziata");
                this.setGameState(GameState.BUILDING_SHIP);

                handleBuildingShip(input);

            }
            default -> System.out.println("Invalid gameState");
        }

    }

    public void handleConnectionTypeInput(ConnectionType connectionType) {
        client = NetworkFactory.create(connectionType, view, inputMessageQueue);

        // Avvia il client (che si occupa di leggere dal socket o direttamente attraverso RMI e mettere i messaggi in coda)
        // TODO: capire come gestire questa eccezione in cui la rete non viene create per qualche motivo
        if (client != null) {
            client.run();
        }
//        } else {
//            throw new NotBoundException("Client not found");
//        }


    }

    public Message handleNicknameInput(String nickname) {
        this.nickname = nickname;
        return new NicknameMessage(nickname, setupClientid);
    }

    public Message handleSetupGame(GameMode mode, int nPlayers) {
        return new GameSetupMessage(game.getIdGame(), nickname, nPlayers, mode);
    }

    private void handleBuildingShip(Input intInput) throws Exception{
        if (intInput instanceof IntInput input){
            int chosenAction = input.getNumber();
            Message message = null;

            switch (chosenAction){
                case 1 -> message = new CoveredComponentMessage(game.getIdGame(), nickname, -1);


                default -> {}
            }

            client.sendToServer(message);
            this.setGameState(GameState.WAITING);
        }
    }

    public void setGameState(GameState gameState) {
        synchronized (this.stateLock) {
            this.gameState = gameState;
        }
    }

    public void setGameState(ClientState state){
        synchronized (this) {
            this.notifyAll();
            this.state = state;
        }
    }

    public GameState getGameState() {
        synchronized (this.stateLock) {
            return this.gameState;
        }
    }

    public void setSetupClientId(int id) {
        this.setupClientid = id;
    }

    public void createGame(int id) {
        game = new Game(id);
    }

    public void setGame(int id, GameMode mode, int nPlayers) {
        game = new Game(id);
        game.setupGame(mode, nPlayers);
    }

    public GameClient getGame() {
        return game;
    }

    public abstract void handleWrongNickname(String nickname);

    public void setNextState(ClientState state){
        this.nextState = state;
    }

    public FlyBoard getFlyBoard(){
        return flyBoard;
    }

    public void createPlayers(Set<String> players){
        flyBoard = FlyBoard.createFlyBoard(game.getGameMode(), players);
    }

    public void handleBookedComponent(String nickname, int idComp, int position){
        ShipBoard shipBoard = flyBoard.getPlayerByUsername(nickname).getShipBoard();
        shipBoard.addBookedComponent(idComp);
    }

    public void handleAddComponent(String nickname, int idComp, Cordinate cordinate, int rotation) {

    }
}
