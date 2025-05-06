package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.ServerController;
import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    private final static Logger logger = LoggerFactory.getLogger(RmiServer.class);

    private final GameManager gameManager = GameManager.getInstance();
    private final ServerController serverController;
    private final BlockingQueue<Message> recivedMessageQueue;

    public RmiServer(BlockingQueue<Message> recivedMessageQueue) throws RemoteException {
        super();
        this.serverController = new ServerController();
        this.recivedMessageQueue = recivedMessageQueue;
    }

    @Override
    public void connect(VirtualClient client) throws Exception {
        serverController.addClient(client);

        logger.info("Client {} has connected to server", client);
    }

    @Override
    public void sendToServer(Message message) throws RemoteException {
        recivedMessageQueue.add(message);
    }
}
