package org.mio.progettoingsoft.network.server.rmi;

import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.client.rmi.VirtualServerRmi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    private final static Logger logger = LoggerFactory.getLogger(RmiServer.class);

    private final ServerController serverController;
    private final BlockingQueue<Message> recivedMessageQueue;

    public RmiServer(BlockingQueue<Message> recivedMessageQueue) throws RemoteException {
        super();
        this.recivedMessageQueue = recivedMessageQueue;
        serverController = ServerController.getInstance();
    }


    @Override
    public void connect(VirtualClient client) throws Exception {
        logger.info("Client {} has connected to server", client);
        serverController.addClientToAccept(client);
    }

    @Override
    public void sendToServer(Message message) throws RemoteException {
        recivedMessageQueue.add(message);
    }
}
