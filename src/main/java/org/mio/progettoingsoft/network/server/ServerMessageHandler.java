package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.network.messages.Message;
import org.mio.progettoingsoft.network.messages.NicknameMessage;
import org.mio.progettoingsoft.network.messages.WelcomeMessage;

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
                    default -> {}
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
