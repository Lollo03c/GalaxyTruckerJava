package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.network.message.*;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

public class ClientMessageHandler implements Runnable{
    private final ClientController clientController;
    private final BlockingQueue<Message> inputMessageQueue;

    public ClientMessageHandler(ClientController clientController, BlockingQueue<Message> inputMessageQueue) {
        this.clientController = clientController;
        this.inputMessageQueue = inputMessageQueue;
    }

    /**
     * Runs the message handler loop. <p>
     * Loops over the queue in a blocking way (waiting for messages when empty) and handles them.
     * <p>
     * This method is supposed to be run on its own thread.
     */
    @Override
    public void run() {
        // thread di gestione dei messaggi in coda dal server
        while(true){
            Message message = inputMessageQueue.poll();
            if (message != null) {
                try {
                    handleMessage(message);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Handles a {@link Message} by checking its validity and handling it according to its type.
     *
     * @param message The {@link Message} to handle
     */
    public void handleMessage(Message message) throws RemoteException {
        switch (message){
            case WelcomeMessage welcomeMessage -> {
                clientController.setGameState(GameState.NICKNAME_REQUEST);
                clientController.setSetupClientId(welcomeMessage.getIdPlayer());
            }

            case GameSetupMessage gameSetupMessage -> {
                clientController.setGameState(GameState.SETUP_GAME);
                clientController.createGame(gameSetupMessage.getIdGame());
            }

            case WaitingForPlayerMessage waitingForPlayerMessage -> {
                clientController.setGame(waitingForPlayerMessage.getIdGame(), waitingForPlayerMessage.getMode(), waitingForPlayerMessage.getnPlayers());
                clientController.setGameState(GameState.PRINT_GAME_INFO);
            }

            case StartGameMessage startGameMessage -> {
                clientController.setGameState(GameState.BUILDING_SHIP);
            }

            case ErrorMessage errorMessage -> {
                if (errorMessage.getErrorType().equals(ErrorType.NICKNAME))
                    clientController.handleWrongNickname(errorMessage.getNickname());
            }

            default -> {

            }
        }
    }
}
