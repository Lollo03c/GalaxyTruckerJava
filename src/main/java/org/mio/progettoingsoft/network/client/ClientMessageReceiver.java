package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.network.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class ClientMessageReceiver implements Runnable{

    private final BlockingQueue<Message> receivedMessages;
    private final ObjectInputStream inputStream;

    public ClientMessageReceiver(ObjectInputStream inputStream, BlockingQueue<Message> receivedMessages){
        this.inputStream = inputStream;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void run(){
        Object receivedObject;

        try {
            while ((receivedObject = inputStream.readObject()) != null) {
                if (receivedObject instanceof Message message){
                    receivedMessages.put(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
