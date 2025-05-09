package org.mio.progettoingsoft.network.client.rmi;

import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.server.rmi.VirtualClientRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;

public class RmiClient extends UnicastRemoteObject implements Client, VirtualClientRmi {
    final VirtualServerRmi server;
    private BlockingQueue<Message> inputMessageQueue;

    public RmiClient(VirtualServerRmi server, BlockingQueue<Message> inputMessageQueue) throws Exception {
        super();
        this.server = server;
        this.inputMessageQueue = inputMessageQueue;

        this.server.connect(this);
    }

    /*
     * METHODS INHERITED FROM Client INTERFACE
     */

    @Override
    public void run() {
        // RMI non serve che stia in ascolto
    }

    // Metodo chiamato dal ClientController per mandare i messaggi al server -> definito in Client
    @Override
    public void sendToServer(Message message) throws RemoteException {
        server.sendToServer(message);
    }

    @Override
    public void close() {

    }

    /*
     * METHODS INHERITED FROM VirtualClient INTERFACE, they're directly called from the server
     */

    // Metodo chiamato dal server per mandare messaggi al client -> definito in VirtualClient
    @Override
    public void showUpdate(Message message) throws RemoteException {
        inputMessageQueue.add(message);
    }
}
