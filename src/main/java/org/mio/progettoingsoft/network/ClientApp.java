package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientApp {
    /*
    * Questa classe si occupa di chiedere all'utente se vuole connettersi via socket o RMI e creare la connessione
    * scelta attraverso NetworkFacotry che implementa il Factory pattern.
    */
    private Client client;
    private final ClientController clientController;
    private final ClientMessageHandler clientMessageHandler;
    private final BlockingQueue<Message> inputMessageQueue;

    public ClientApp(boolean isGui) {
        // TODO: andrà fatto con factory in base a isGui
        // view = new Tui();
        // view = new VirtualView(isGui);

        this.inputMessageQueue = new LinkedBlockingQueue<>();
        this.clientController = ClientController.create(isGui);
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
