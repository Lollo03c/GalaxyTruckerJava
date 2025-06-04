package org.mio.progettoingsoft.network.server.socket;

import org.mio.progettoingsoft.advCards.sealed.SldStardust;
import org.mio.progettoingsoft.network.messages.*;
import org.mio.progettoingsoft.network.server.ServerController;

import java.util.concurrent.BlockingQueue;

public class ServerMessageHandler implements Runnable {
    private final BlockingQueue<Message> receivedMessages;
    private final ServerController serverController;

    public ServerMessageHandler(ServerController serverController, BlockingQueue<Message> receivedMessages){
        this.serverController = serverController;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void run(){
        while (true){
            try {
                Message message = receivedMessages.take();

                switch (message){
                    case NicknameMessage nicknameMessage -> {
                        serverController.handleNickname(nicknameMessage.getIdClient(), nicknameMessage.getNickname());
                    }

                    case GameInfoMessage gameInfoMessage -> {
                        serverController.handleGameInfo(gameInfoMessage.getGameInfo(), gameInfoMessage.getNickname());
                    }

                    case ComponentMessage componentMessage -> {
                        switch (componentMessage.getAction()) {
                            case ADD -> {
                                serverController.addComponent(componentMessage.getGameId(), componentMessage.getNickname(),
                                        componentMessage.getIdComp(), componentMessage.getCordinate(), componentMessage.getRotations());
                            }

                            case REMOVE -> {}

                            case DISCARD -> {
                                serverController.discardComponent(componentMessage.getGameId(), componentMessage.getIdComp());
                            }

                            case COVERED -> {
                                serverController.getCoveredComponent(componentMessage.getGameId(), componentMessage.getNickname());
                            }
                        }
                    }

                    case DeckMessage deckMessage -> {
                        switch (deckMessage.getAction()){
                            case BOOK -> serverController.bookDeck(deckMessage.getGameId(), deckMessage.getNickname(), deckMessage.getDeckNumber());
                            case UNBOOK -> serverController.freeDeck(deckMessage.getGameId(), deckMessage.getNickname(), deckMessage.getDeckNumber());
                        }

                    }
                    case StardustMessage stardustMessage -> {
                        serverController.applyStardust(stardustMessage.getGameId(), stardustMessage.getCard());
                    }
                    default -> {}
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
