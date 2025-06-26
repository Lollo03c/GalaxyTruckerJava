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

/**
 * Manages the Socket server operations, including listening for incoming client connections
 * and dispatching received messages for processing.
 * This class sets up a server socket and spawns threads for each new client and for
 * handling all incoming messages.
 */
public class SocketServer {
    BlockingQueue<Message> receivedMessages;
    ServerMessageHandler serverMessageHandler;
    ServerController controller;
    private final ConnectionInfo connectionInfo;

    /**
     * Constructs a new {@code SocketServer}.
     * Initializes the {@link BlockingQueue} for messages, gets the singleton {@link ServerController} instance,
     * and creates a {@link ServerMessageHandler} to process incoming messages.
     * @param connectionInfo The {@link ConnectionInfo} object containing the server's network details.
     */
    public SocketServer(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
        this.receivedMessages = new LinkedBlockingQueue<>();
        this.controller = ServerController.getInstance();
        this.serverMessageHandler = new ServerMessageHandler(this.controller, this.receivedMessages);
    }

    /**
     * Starts the Socket server.
     * This method performs the following actions:
     * <ol>
     * <li>Creates a {@link ServerSocket} to listen for client connections on the configured port.</li>
     * <li>Starts a dedicated thread for the {@link ServerMessageHandler} to asynchronously process
     * messages placed in the {@code receivedMessages} queue. This thread is set as a daemon thread.</li>
     * <li>Logs the server startup information.</li>
     * <li>Enters an infinite loop to continuously accept new client connections.</li>
     * <li>For each new client connection, it creates a {@link SocketClientHandler} and starts a new
     * daemon thread to manage communication with that specific client.</li>
     * </ol>
     * @throws IOException If an I/O error occurs when opening the server socket or accepting connections.
     */
    public void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(connectionInfo.getSocketPort());

        Thread serverMessageThread = new Thread(serverMessageHandler, "message-handler");
        serverMessageThread.setDaemon(true);
        serverMessageThread.start();

        Logger.info("SERVER SOCKET STARTED | Port: " + connectionInfo.getSocketPort() + " | IP: " + connectionInfo.getIpHost() + " | Server: " + connectionInfo.getServerName());

        while (true){
            Socket clientSocket = serverSocket.accept();

            SocketClientHandler clientHandler = new SocketClientHandler(clientSocket, receivedMessages);
            Thread clientHandlerThread = new Thread(clientHandler, "message-handler");
            clientHandlerThread.setDaemon(true);
            clientHandlerThread.start();
        }
    }
}
