package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.network.server.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class ServerMessageHandler implements Runnable{
    private final BlockingQueue<Message> receivedMessage;
    private final ObjectInputStream input;

    public ServerMessageHandler(ObjectInputStream input, BlockingQueue<Message> receivedMessage){
        this.input = input;
        this.receivedMessage = receivedMessage;
    }

    @Override
    public void run(){
        while (true){
            try{
                Message message = (Message) input.readObject();

                this.receivedMessage.add(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }


}