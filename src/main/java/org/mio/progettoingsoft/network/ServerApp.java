package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;
import org.mio.progettoingsoft.network.rmi.server.RmiServer;
import org.mio.progettoingsoft.network.socket.server.SocketServer;

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
        System.out.println("Server started on port " + RMI_PORT);
    }

    private void runSocketServer() throws IOException {
        ServerSocket listenSocket = new ServerSocket(SOCKET_PORT);
        System.out.println("Socket server started on port " + SOCKET_PORT);
        SocketServer s1 = new SocketServer(listenSocket);
        try{
            s1.runServer();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
