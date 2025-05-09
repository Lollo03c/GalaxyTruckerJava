package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.network.message.Message;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientApp {
    private final ClientController clientController;
    private final ClientMessageHandler clientMessageHandler;
    private final BlockingQueue<Message> inputMessageQueue;

    public ClientApp(boolean isGui) {
        this.clientController = ClientController.create(isGui);

        /*
         * this queue contains all the incoming messages:
         * the messages are enqueued by the client (rmiClient/socketClient) object in clientController
         * the messages are dequeued by the clientMessageHandler
         */
        this.inputMessageQueue = new LinkedBlockingQueue<>();
        this.clientController.setMessageQueue(inputMessageQueue);

        this.clientMessageHandler = new ClientMessageHandler(clientController, inputMessageQueue);
    }

    public void run() throws IOException, NotBoundException {
        // Thread che gestisce i messaggi in arrivo
        Thread messageHandlerThread = new Thread(clientMessageHandler, "message-handler");
        messageHandlerThread.setDaemon(true);
        messageHandlerThread.start();

        // Run the controller loop in this thread
        clientController.run();
    }
}
