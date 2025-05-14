package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.network.server.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class ClientMessageHandler implements Runnable{
    private final BlockingQueue<Message> receivedMessages;
    private final ObjectInputStream inputStream;

    public ClientMessageHandler(ObjectInputStream inputStream, BlockingQueue<Message> receivedMessages){
        this.inputStream = inputStream;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void run(){
        while (true){

            Message message = null;
            try {
                message = (Message) inputStream.readObject();

                receivedMessages.put(message);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }


}
