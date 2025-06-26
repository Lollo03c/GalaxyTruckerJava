package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.network.server.rmi.RmiServer;
import org.mio.progettoingsoft.network.server.socket.SocketServer;
import org.mio.progettoingsoft.utils.ConnectionInfo;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * The main application class for the server, responsible for initializing and starting
 * both the RMI and Socket-based server components.
 * This class acts as an orchestrator for the different network communication layers.
 */
public class ServerApp implements Runnable {
    private final RmiServer rmiServer;
    private final SocketServer socketServer;

    /**
     * Constructs a new ServerApp.
     * Initializes the RMI and Socket server components with the provided connection information.
     * It also sets the RMI server hostname system property.
     * @param connectionInfo The {@link ConnectionInfo} object containing the IP address and port details for the server.
     * @throws RemoteException if an error occurs during RMI server initialization.
     */
    public ServerApp(ConnectionInfo connectionInfo) throws RemoteException {
        System.setProperty("java.rmi.server.hostname", connectionInfo.getIpHost());
        this.rmiServer = new RmiServer(connectionInfo);
        this.socketServer = new SocketServer(connectionInfo);
    }

    /**
     * Starts both the RMI and Socket servers.
     * This method is designed to be run in a separate thread, typically by passing an instance
     * of ServerApp to a new {@link Thread}.
     * The RMI server is started first, followed by the Socket server.
     * If an {@link IOException} occurs during the Socket server startup, it is wrapped in a
     * {@link RuntimeException} and re-thrown.
     */
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
