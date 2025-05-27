package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.network.server.rmi.RmiServer;
import org.mio.progettoingsoft.network.server.socket.SocketServer;
import org.mio.progettoingsoft.utils.ConnectionInfo;

import java.io.IOException;
import java.rmi.RemoteException;

public class ServerApp implements Runnable {
    private final RmiServer rmiServer;
    private final SocketServer socketServer;

    public ServerApp(ConnectionInfo connectionInfo) throws RemoteException {
        this.rmiServer = new RmiServer(connectionInfo);
        this.socketServer = new SocketServer(connectionInfo);
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
