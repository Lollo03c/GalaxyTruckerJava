package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.Tui;
import org.mio.progettoingsoft.views.VirtualView;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientApp {
    /* Questa classe si occupa di chiedere all'utente se vuole connettersi via socket o RMI e creare la connessione
    * scelta attraverso NetworkFacotry che implementa il Factory pattern.
    */
    private final boolean isGui;
    private Client client;
    private final ClientController controller;
    private final MessageHandler messageHandler;
    private final VirtualView view;
    private final BlockingQueue<Message> serverMessageQueue;

    public ClientApp(boolean isGui) throws IOException, NotBoundException {
        this.isGui = isGui;
        this.controller = new ClientController();
        this.serverMessageQueue = new LinkedBlockingQueue<>();

        this.messageHandler = new MessageHandler(serverMessageQueue);

        // andra fatto con factory in base a isGui
        view = new Tui();
        // view = new VirtualView(isGui);
    }

    public void run() throws IOException, NotBoundException {
        ConnectionType connectionType = view.askConnectionType();
        client = NetworkFactory.create(connectionType, view, serverMessageQueue);
        client.run();
    }
}
