package org.mio.progettoingsoft.network.client.socket;

import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.messages.*;
import org.mio.progettoingsoft.network.server.VirtualServer;
import org.mio.progettoingsoft.utils.ConnectionInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketClient implements Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private SocketServerHandler serverHandler;
    private final ClientController controller;
    private final ConnectionInfo connectionInfo;

    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public SocketClient(ConnectionInfo connectionInfo) throws RemoteException {
        controller = ClientController.getInstance();
        this.connectionInfo = connectionInfo;
    }

    /**
     * METHODS INHERITED FROM Client INTERFACE
     */

    @Override
    public VirtualServer getServer() {
        return serverHandler;
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket(connectionInfo.getIpHost(), connectionInfo.getSocketPort());
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.serverHandler = new SocketServerHandler(out,in);

        ClientMessageReceiver clientMessageReceiver = new ClientMessageReceiver(in, receivedMessages);
        Thread serverMessageThread = new Thread(clientMessageReceiver, "message-receiver");
        serverMessageThread.setDaemon(true);
        serverMessageThread.start();

        Thread serverReceiverThread = new Thread(this::handleMessage, "message-handler");
        serverReceiverThread.setDaemon(true);
        serverReceiverThread.start();

        serverHandler.sendWelcome();
    }

    /**
     * method executed by a different thread to handle messages
     */

    private void handleMessage() {
        while (true) {
            try {
                Message message = receivedMessages.take();

                switch (message) {
                    case WelcomeMessage welcomeMessage -> {
                        controller.setIdClient(welcomeMessage.getClientId());
                    }
                    case StateMessage stateMessage -> {
                        controller.setState(stateMessage.getState());
                    }

                    case NicknameMessage nicknameMessage -> {
                        controller.setNickname(nicknameMessage.getNickname());
                    }

                    case GameIdMessage gameIdMessage -> {
                        controller.setGameId(gameIdMessage.getGameId());
                    }

                    case FlyBoardMessage flyBoardMessage -> {
                        controller.setFlyBoard(flyBoardMessage.getMode(), flyBoardMessage.getPlayers(), flyBoardMessage.getDecks());
                    }

                    case ComponentMessage componentMessage -> {
                        switch (componentMessage.getAction()) {
                            case COVERED -> controller.setInHandComponent(componentMessage.getIdComp());
                            case REMOVE -> {
                            }
                            case ADD ->
                                    controller.addOtherPlayersComponent(componentMessage.getNickname(), componentMessage.getIdComp(),
                                            componentMessage.getCordinate(), componentMessage.getRotations());


                            case ADD_UNCOVERED -> controller.addUncoveredComponent(componentMessage.getIdComp());
                        }
                    }

                    case DeckMessage deckMessage -> {
                        switch (deckMessage.getAction()) {
                            case BOOK -> {
                                controller.setInHandDeck(deckMessage.getDeckNumber());
                            }

                            case REMOVE_FROM_CLIENT -> {
                                controller.removeDeck(deckMessage.getDeckNumber());
                            }

                            case UNBOOK -> controller.addAvailableDeck(deckMessage.getDeckNumber());
                        }
                    }
                    case AvailablePlacesMessage availablePlacesMessage -> {
                        controller.setAvailablePlaces(availablePlacesMessage.getAvailablePlaces());
                    }

                    case AdvancePlayerMessage advancePlayerMessage ->
                        executor.submit(() -> controller.advancePlayer(advancePlayerMessage.getNickname(), advancePlayerMessage.getSteps()));

                    default -> {
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
