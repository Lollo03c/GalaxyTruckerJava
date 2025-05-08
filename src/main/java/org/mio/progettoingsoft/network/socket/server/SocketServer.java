package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.ServerController;
import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.WelcomeMessage;
import org.mio.progettoingsoft.network.rmi.server.RmiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
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

//            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
//            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            SocketClientHandler handler = new SocketClientHandler(clientSocket, recivedMessageQueue);

            logger.info("Client {} has connected to server", handler);

            //thread che mi resta sempre attivo e che legge i messaggi dal server al client
            new Thread(() -> {
                try {
                    handler.runVirtualClient();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void broadcastUpdate(Integer value) {
        /*
        synchronized (this.clients) {
            for (var client : this.clients) {
                //client.send(value);
            }
        }
        */
    }

    public void broadcastError() {
        /*
        synchronized (this.clients) {
            for (VirtualViewSocket client : clients) {
                try {
                    client.reportError("already reset");
                } catch (java.rmi.RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
         */
    }
}
