package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

public class MessageHandler implements Runnable{
    private final ClientController clientController;
    private final BlockingQueue<Message> messageQueue;

    public MessageHandler(ClientController clientController, BlockingQueue<Message> messageQueue) {
        this.clientController = clientController;
        this.messageQueue = messageQueue;
    }

    /**
     * Runs the message handler loop. <p>
     * Loops over the queue in a blocking way (waiting for messages when empty) and handles them.
     * <p>
     * This method is supposed to be run on its own thread.
     */
    @Override
    public void run() {
        // thread di gestione dei messaggi in coda dal server
        while(true){
            Message message = messageQueue.poll();
            if (message != null) {
                try {
                    handleMessage(message);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Handles a {@link Message} by checking its validity and handling it according to its type.
     *
     * @param message The {@link Message} to handle
     */
    public void handleMessage(Message message) throws RemoteException {

    }
}
