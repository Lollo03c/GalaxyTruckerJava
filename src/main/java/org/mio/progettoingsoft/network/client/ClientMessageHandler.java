package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.state.*;
import org.mio.progettoingsoft.network.message.*;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

public class ClientMessageHandler implements Runnable {
    private final ClientController clientController;
    private final BlockingQueue<Message> inputMessageQueue;

    public ClientMessageHandler(ClientController clientController, BlockingQueue<Message> inputMessageQueue) {
        this.clientController = ClientController.get();
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
        while (true) {
            Message message = null;
            try {
                message = inputMessageQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (message != null) {
                try {


//                    if (message.getNickname().equals(clientController.getNickname()) ||
//                            message.getNickname().equals(Message.getBroadcastAddress())) {
                        handleMessage(message);
  //                  }

                    synchronized (clientController) {
                        clientController.notifyAll();
                    }
                } catch(RemoteException e){
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
        switch (message) {
            case WelcomeMessage welcomeMessage -> {
                clientController.setNextState(new SetNicknameState());
//                clientController.setGameState(GameState.NICKNAME_REQUEST);
                clientController.setSetupClientId(welcomeMessage.getIdPlayer());
            }

            case GameSetupMessage gameSetupMessage -> {
                clientController.setNextState(new SetupGameState());
                clientController.setGameState(GameState.SETUP_GAME);
                clientController.createGame(gameSetupMessage.getIdGame());
            }

            case WaitingForPlayerMessage waitingForPlayerMessage -> {
                clientController.setGame(waitingForPlayerMessage.getIdGame(), waitingForPlayerMessage.getMode(), waitingForPlayerMessage.getnPlayers());
                clientController.setNextState(new WaitingState(WaitingState.WaitingType.PLAYERS));
                clientController.setGameState(GameState.PRINT_GAME_INFO);
            }

            case StartGameMessage startGameMessage -> {
                clientController.createPlayers(startGameMessage.getPlayers());

                clientController.setNextState(new BuildingShipState());
                clientController.setGameState(GameState.BUILDING_SHIP);
            }

            case CoveredComponentMessage coveredComponentMessage -> {
                clientController.setNextState(new AddComponentState(coveredComponentMessage.getIdComp()));
            }

            case AddBookedMessage addBookedMessage -> {
                if (!addBookedMessage.getNickname().equals(clientController.getNickname()))
                    clientController.handleBookedComponent(addBookedMessage.getNickname(), addBookedMessage.getAddedCompId(), addBookedMessage.getToPosition());
            }

            case AddComponentMessage addComponentMessage -> {
                if (!addComponentMessage.getNickname().equals(clientController.getNickname()))
                    clientController.handleAddComponent(addComponentMessage.getNickname(), addComponentMessage.getIdComp()
                    , addComponentMessage.getCordinate(), addComponentMessage.getRotations());
            }

            case ErrorMessage errorMessage -> {
                switch (errorMessage.getErrorType()){
                    case NICKNAME -> {
                        clientController.handleWrongNickname(errorMessage.getNickname());
                    }
                    default -> throw new RuntimeException("Unexpected error");
                }
            }

            default -> {

            }
        }
    }
}
