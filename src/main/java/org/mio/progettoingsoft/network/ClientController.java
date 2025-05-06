package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.NicknameMessage;
import org.mio.progettoingsoft.views.VirtualView;

import java.rmi.NotBoundException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ClientController implements Runnable {
    private static ClientController instance;

    protected GameState gameState = GameState.START;
    protected Client client;
    protected VirtualView view;
    protected BlockingQueue<Message> inputMessageQueue;
    /*
     * nickname
     * gamecode
     * sender -> classe interna usata per costruire i messaggi e spedirli
     */

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

    protected void handleInput(String input) throws Exception {
        switch (gameState) {
            case START -> handleConnectionTypeInput(input);
            case NICKNAME_REQUEST -> handleNicknameInput(input);
            case WAITING_GAME -> {}
            default -> System.out.println("Invalid gameState");
        }
    }

    private void handleConnectionTypeInput(String input) throws Exception {
        boolean isRmi = (Integer.parseInt(input) == 1);

        ConnectionType connectionType = new ConnectionType(isRmi, "127.0.0.1", isRmi ? 1099 : 1234, "localhost");
        client = NetworkFactory.create(connectionType, view, inputMessageQueue);

        // Avvia il client (che si occupa di leggere dal socket o direttamente attraverso RMI e mettere i messaggi in coda)
        // TODO: capire come gestire questa eccezione in cui la rete non viene create per qualche motivo
        if(client != null) {
            client.run();
        } else {
            throw new NotBoundException("Client not found");
        }

        gameState = GameState.NICKNAME_REQUEST;
    }

    private void handleNicknameInput(String input) throws Exception {
        client.sendInput(new NicknameMessage(input, 1));
        gameState = GameState.WAITING_GAME;
    }
}
