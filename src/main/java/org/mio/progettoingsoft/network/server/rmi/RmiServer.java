package org.mio.progettoingsoft.network.server.rmi;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.WelcomeMessage;
import org.mio.progettoingsoft.network.client.rmi.VirtualServerRmi;
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
        int idClient = GameManager.getInstance().getNextIdPlayer();
        client.showUpdate(new WelcomeMessage(idClient));

        logger.info("Client {} has connected to server", client);
    }

    @Override
    public void sendToServer(Message message) throws RemoteException {
        recivedMessageQueue.add(message);
    }
}
