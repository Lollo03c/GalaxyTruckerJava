package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.Client;
import org.mio.progettoingsoft.network.ClientController;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.server.VirtualClientRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;

public class RmiClient extends UnicastRemoteObject implements Client, VirtualClientRmi {
    final VirtualServerRmi server;
    private BlockingQueue<Message> messageQueue;
    private ClientController clientController;

    public RmiClient(VirtualServerRmi server, BlockingQueue<Message> messageQueue) throws RemoteException {
        super();
        this.server = server;
        this.messageQueue = messageQueue;
        this.clientController = ClientController.get();

        this.server.connect(this);
    }

    @Override
    public void run() {
        // RMI non serve che stia in ascolto
    }

    // Metodo chiamato dal ClientController per mandare i messaggi al server -> definito in Client
    @Override
    public void sendInput(Message message) throws RemoteException {
        server.sendToServer(message);
    }

    @Override
    public void close() {

    }

    // Metodo chiamato dal server per mandare messaggi al client -> definito in VirtualClient
    @Override
    public void sendToClient(Message message) throws RemoteException {
        messageQueue.add(message);
    }

    @Override
    public void reportError(String details) throws RemoteException {

    }

}
