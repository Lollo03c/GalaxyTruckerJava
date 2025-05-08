package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.Client;
import org.mio.progettoingsoft.network.ClientController;
import org.mio.progettoingsoft.network.message.ErrorMessage;
import org.mio.progettoingsoft.network.message.ErrorType;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.server.VirtualClientRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;

public class RmiClient extends UnicastRemoteObject implements Client, VirtualClientRmi {
    final VirtualServerRmi server;
    private BlockingQueue<Message> messageQueue;
    private ClientController clientController;

    public RmiClient(VirtualServerRmi server, BlockingQueue<Message> messageQueue) throws Exception {
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
    public void sendError(int idGame, String nickname, ErrorType errorType) throws RemoteException {
        sendInput(new ErrorMessage(idGame, nickname, errorType));
    }

    @Override
    public void close() {

    }

    // Metodo chiamato dal server per mandare messaggi al client -> definito in VirtualClient
    @Override
    public void showUpdate(Message message) {
        messageQueue.add(message);
    }

    @Override
    public void reportError(int idGame, String nickname, ErrorType errorType) throws Exception{
        messageQueue.add(new ErrorMessage(idGame, nickname, errorType));
    }

}
