package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.message.GameSetupInput;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.NicknameMessage;

import java.util.concurrent.BlockingQueue;

public class ServerMessageHandler implements Runnable {
    private final ServerController serverController;
    private final GameManager gameManager;
    private final BlockingQueue<Message> receivedMessageQueue;

    public ServerMessageHandler(BlockingQueue<Message> receivedMessageQueue) {
        serverController = ServerController.getInstance();
        gameManager = GameManager.getInstance();
        this.receivedMessageQueue = receivedMessageQueue;
    }

    @Override
    public void run() {
        while (true) {
            if (!receivedMessageQueue.isEmpty()) {
                Message message = receivedMessageQueue.poll();
                handleMessage(message);
            }
        }
    }

    private void handleMessage(Message message) {
        int idGame;

        switch (message) {
            case NicknameMessage nMessage -> {
                serverController.addPlayer(nMessage.getNickname(), nMessage.getIdPlayer());
            }
            case GameSetupInput setupMessage -> {
                serverController.setupGame(setupMessage);
            }
            default -> {
                idGame = message.getIdGame();
                Game gameToSend = gameManager.getOngoingGames().get(idGame);

                if (gameToSend != null) {
                    gameToSend.addReceivedMessage(message);
                }
            }
        }
    }
}
