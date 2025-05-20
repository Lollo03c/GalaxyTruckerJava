package org.mio.progettoingsoft.network.server.socket;

import org.mio.progettoingsoft.network.messages.Message;
import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketServer {
    BlockingQueue<Message> receivedMessages;
    ServerMessageHandler serverMessageHandler;
    ServerController controller;
    private final ConnectionInfo connectionInfo;

    public SocketServer(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
        this.receivedMessages = new LinkedBlockingQueue<>();
        this.controller = ServerController.getInstance();
        this.serverMessageHandler = new ServerMessageHandler(this.controller, this.receivedMessages);
    }

    public void startServer() throws IOException {
        final int port = connectionInfo.socketPort();
        ServerSocket serverSocket = new ServerSocket(port);

        Thread serverMessageThread = new Thread(serverMessageHandler, "message-handler");
        serverMessageThread.setDaemon(true);
        serverMessageThread.start();

        Logger.info("Server Socket running on port " + port);

        while (true){
            Socket clientSocket = serverSocket.accept();

            SocketClientHandler clientHandler = new SocketClientHandler(clientSocket, receivedMessages);
            Thread clientHandlerThread = new Thread(clientHandler, "message-handler");
            clientHandlerThread.setDaemon(true);
            clientHandlerThread.start();
        }
    }
}
