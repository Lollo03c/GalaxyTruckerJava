package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.ServerMain;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.client.rmi.VirtualServerRmi;
import org.mio.progettoingsoft.network.server.rmi.RmiServer;
import org.mio.progettoingsoft.network.server.socket.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerApp {
    private static final int RMI_PORT = 1099;
    private static final String SERVER_NAME = "localhost";
    private static final String HOST = "127.0.0.1";
    private static final int SOCKET_PORT = 1234;

    private final BlockingQueue<Message> recivedMessageQueue;
    private final ServerMessageHandler serverMessageHandler;

    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

    public ServerApp() {
        GameManager.create();
        ServerController.create();

        recivedMessageQueue = new LinkedBlockingQueue<>();
        serverMessageHandler = new ServerMessageHandler(recivedMessageQueue);
    }

    public void run() throws IOException {
        // Thread che gestisce i messaggi in entrata
        Thread messageHandlerThread = new Thread(serverMessageHandler, "server-message-handler");
        messageHandlerThread.setDaemon(true);
        messageHandlerThread.start();

        runRmiServer();
        runSocketServer();
    }

    private void runRmiServer() throws RemoteException {
        VirtualServerRmi server = new RmiServer(recivedMessageQueue);
        Registry registry = LocateRegistry.createRegistry(RMI_PORT);
        registry.rebind(SERVER_NAME, server);

        logger.info("RMI server {} running on port {}", SERVER_NAME, RMI_PORT);
    }

    private void runSocketServer() throws IOException {
        ServerSocket listenSocket = new ServerSocket(SOCKET_PORT);

        logger.info("Socket server {} running on port {}", SERVER_NAME, SOCKET_PORT);

        SocketServer serverSocket = new SocketServer(listenSocket, recivedMessageQueue);
        try{
            serverSocket.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
