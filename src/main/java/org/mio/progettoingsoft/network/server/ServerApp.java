package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.network.server.rmi.RmiServer;
import org.mio.progettoingsoft.network.server.socket.SocketServer;

import java.io.IOException;

public class ServerApp implements Runnable {
    private static final int RMI_PORT = 1099;
    private static final String SERVER_NAME = "localhost";
    private static final String HOST = "127.0.0.1";
    private static final int SOCKET_PORT = 1234;

    private final RmiServer rmiServer;
    private final SocketServer socketServer;

    public ServerApp() {
        this.rmiServer = new RmiServer();
        this.socketServer = new SocketServer();
    }

    @Override
    public void run(){
        rmiServer.startServer();
        try {
            socketServer.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
