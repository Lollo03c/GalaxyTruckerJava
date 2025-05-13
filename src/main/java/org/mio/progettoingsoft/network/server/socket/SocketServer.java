package org.mio.progettoingsoft.network.server.socket;

import org.mio.progettoingsoft.network.server.ServerController;
import org.mio.progettoingsoft.network.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SocketServer {
    private final ServerSocket listenSocket;
    private final BlockingQueue<Message> recivedMessageQueue;
    private final ServerController serverController = ServerController.getInstance();

    private final static Logger logger = LoggerFactory.getLogger(SocketServer.class);

    public SocketServer(ServerSocket listenSocket, BlockingQueue<Message> recivedMessageQueue) {
        this.listenSocket = listenSocket;
        this.recivedMessageQueue = recivedMessageQueue;
    }

    public void runServer() throws Exception {
        while (true) {
            Socket clientSocket = listenSocket.accept();

            SocketClientHandler handler = new SocketClientHandler(clientSocket, recivedMessageQueue);

            logger.info("Client {} has connected to server", handler);

            //create a new thread which waits for messages from the client
            new Thread(() -> {
                try {
                    handler.runVirtualClient();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
