package org.mio.progettoingsoft.network.client.socket;

import org.mio.progettoingsoft.network.client.ClientController;
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
            ClientController.getInstance().notifyCrash("");
        } catch (ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
