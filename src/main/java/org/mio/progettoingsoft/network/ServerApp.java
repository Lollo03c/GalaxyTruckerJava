package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;
import org.mio.progettoingsoft.network.rmi.server.RmiServer;
import org.mio.progettoingsoft.network.socket.server.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApp {
    private static final int RMI_PORT = 1099;
    private static final String SERVER_NAME = "localhost";
    private static final String HOST = "127.0.0.1";
    private static final int SOCKET_PORT = 1234;

    private final ServerController serverController;

    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

    public ServerApp() {
        this.serverController = new ServerController();
    }

    public void run() throws IOException {
        runRmiServer();
        runSocketServer();
    }

    private void runRmiServer() throws RemoteException {
        VirtualServerRmi server = new RmiServer();
        Registry registry = LocateRegistry.createRegistry(RMI_PORT);
        registry.rebind(SERVER_NAME, server);
        logger.info("RMI server {} running on port {}", SERVER_NAME, RMI_PORT);
    }

    private void runSocketServer() throws IOException {
        ServerSocket listenSocket = new ServerSocket(SOCKET_PORT);
        logger.info("Socket server {} running on port {}", SERVER_NAME, SOCKET_PORT);
        SocketServer serverSocket = new SocketServer(listenSocket);
        try{
            serverSocket.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
