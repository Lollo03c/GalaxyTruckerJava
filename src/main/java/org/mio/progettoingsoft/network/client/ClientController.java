package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.interfaces.GameClient;
import org.mio.progettoingsoft.network.ConnectionType;
import org.mio.progettoingsoft.network.GuiController;
import org.mio.progettoingsoft.network.NetworkFactory;
import org.mio.progettoingsoft.network.TuiController;
import org.mio.progettoingsoft.network.input.Input;
import org.mio.progettoingsoft.network.input.SetupInput;
import org.mio.progettoingsoft.network.input.StringInput;
import org.mio.progettoingsoft.network.message.GameSetupMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.NicknameMessage;
import org.mio.progettoingsoft.views.VirtualView;

import java.rmi.NotBoundException;
import java.util.concurrent.BlockingQueue;

public abstract class ClientController implements Runnable {
    /**
     * SINGLETON IMPLEMENTATION
     * */
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

    // connection parameters - start
    private final String hostAddress = "127.0.0.1";
    private final int rmiPort = 1099;
    private final int socketPort = 1234;
    private final String serverName = "localhost";
    // connection parameters - en

    protected GameState gameState = GameState.START;
    private Client client;
    private BlockingQueue<Message> inputMessageQueue;
    private String nickname;
    private VirtualView view;

    private int setupClientid;

    private GameClient game;


    public void setGameState(GameState gameState) {
        synchronized (gameState) {
            this.gameState = gameState;
        }
    }

    protected void handleInput(Input input) throws Exception {
        switch (gameState) {
            case START -> handleConnectionTypeInput(input);
            case NICKNAME_REQUEST -> handleNicknameInput(input);
            case SETUP_GAME -> handleSetupGame(input);
            case PRINT_GAME_INFO -> {}
            case WAITING -> { Thread.sleep(10);  }
            case BUILDING_SHIP -> {
                System.out.println("partita iniziata");
                setGameState(GameState.WAITING);
            }
            default -> System.out.println("Invalid gameState");
        }
    }

    private void handleConnectionTypeInput(Input input) throws Exception {
        if (input instanceof  StringInput stringInput) {
            boolean isRmi = (Integer.parseInt(stringInput.getString()) == 1);

            ConnectionType connectionType = new ConnectionType(isRmi, hostAddress, isRmi ? rmiPort : socketPort, serverName);
            client = NetworkFactory.create(connectionType, view, inputMessageQueue);

            // Avvia il client (che si occupa di leggere dal socket o direttamente attraverso RMI e mettere i messaggi in coda)
            // TODO: capire come gestire questa eccezione in cui la rete non viene create per qualche motivo
            if (client != null) {
                client.run();
            } else {
                throw new NotBoundException("Client not found");
            }
            gameState = GameState.WAITING;
        }
    }

    private void handleNicknameInput(Input input) throws Exception {
        if (input instanceof StringInput stringInput) {
            nickname = stringInput.getString();
            client.sendToServer(new NicknameMessage(nickname, setupClientid));
            gameState = GameState.WAITING;
        }
    }

    private void handleSetupGame(Input input) throws Exception{
        if (input instanceof SetupInput setupInput){
            Message message = new GameSetupMessage(game.getIdGame(), nickname, setupInput.getnPlayers(), setupInput.getGameMode());
            client.sendToServer(message);
            gameState = GameState.WAITING;
        }
    }

    public void setSetupClientId(int id){
        this.setupClientid = id;
    }

    public void createGame(int id){
        game = new Game(id);
    }

    public void setGame(int id, GameMode mode, int nPlayers){
        game = new Game(id);
        game.setupGame(mode, nPlayers);
    }


    public GameClient getGame(){
        return game;
    }

    public abstract void handleWrongNickname(String nickname);
}
