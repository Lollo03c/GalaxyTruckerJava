package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.network.messages.*;

import java.util.concurrent.BlockingQueue;

public class ServerMessageHandler implements Runnable {
    private final BlockingQueue<Message> receivedMessages;
    private final Server server;

    public ServerMessageHandler(Server server, BlockingQueue<Message> receivedMessages){
        this.server = server;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void run(){
        while (true){
            try {
                Message message = receivedMessages.take();

                switch (message){
                    case NicknameMessage nicknameMessage -> {
                        server.handleNickname(nicknameMessage.getIdClient(), nicknameMessage.getNickname());
                    }

                    case GameInfoMessage gameInfoMessage -> {
                        server.handleGameInfo(gameInfoMessage.getGameInfo());
                    }

                    case ComponentMessage componentMessage -> {
                        switch (componentMessage.getAction()) {
                            case ADD -> {
                                server.addComponent(componentMessage.getGameId(), componentMessage.getNickname(),
                                        componentMessage.getIdComp(), componentMessage.getCordinate(), componentMessage.getRotations());
                            }

                            case REMOVE -> {}

                            case DISCARD -> {
                                server.discardComponent(componentMessage.getGameId(), componentMessage.getIdComp());
                            }

                            case COVERED -> {
                                server.getCoveredComponent(componentMessage.getGameId(), componentMessage.getNickname());
                            }
                        }
                    }

                    case DeckMessage deckMessage -> {
                        switch (deckMessage.getAction()){
                            case BOOK -> server.bookDeck(deckMessage.getGameId(), deckMessage.getNickname(), deckMessage.getDeckNumber());
                            case UNBOOK -> server.freeDeck(deckMessage.getGameId(), deckMessage.getNickname(), deckMessage.getDeckNumber());
                        }

                    }
                    default -> {}
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
