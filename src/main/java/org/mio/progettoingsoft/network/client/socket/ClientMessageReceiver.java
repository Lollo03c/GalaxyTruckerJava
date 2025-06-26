package org.mio.progettoingsoft.network.client.socket;

import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

/**
 * {@code ClientMessageReceiver} is a {@link Runnable} that continuously reads
 * messages from a server's {@link ObjectInputStream} and places them into
 * a {@link BlockingQueue} for further processing by the client's message handler.
 * It's designed to run in a separate thread to asynchronously receive incoming data.
 */
public class ClientMessageReceiver implements Runnable{
    private final BlockingQueue<Message> receivedMessages;
    private final ObjectInputStream inputStream;

    /**
     * Constructs a new {@code ClientMessageReceiver}.
     * @param inputStream The {@link ObjectInputStream} from which to read messages.
     * @param receivedMessages The {@link BlockingQueue} to which received messages will be added.
     */
    public ClientMessageReceiver(ObjectInputStream inputStream, BlockingQueue<Message> receivedMessages){
        this.inputStream = inputStream;
        this.receivedMessages = receivedMessages;
    }

    /**
     * The main execution method for the message receiver thread.
     * It continuously reads objects from the input stream. If an object is a {@link Message}
     * instance, it is added to the {@code receivedMessages} queue.
     */
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
