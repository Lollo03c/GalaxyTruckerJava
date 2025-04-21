package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.network.ServerController;
import org.mio.progettoingsoft.network.message.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketServer {
    private final ServerSocket listenSocket;
    private final ServerController controller;
    private final List<SocketClientHandler> clients = new ArrayList<>();
    private final BlockingQueue<Message> messageQueue;


    public SocketServer(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
        this.controller = new ServerController();
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    public void runServer() throws IOException {
        Socket clientSocket = null;
        while ((clientSocket = this.listenSocket.accept()) != null) {

            SocketClientHandler handler = new SocketClientHandler(
                    this.controller,
                    this,
                    clientSocket
            );

            synchronized (this.clients){
                clients.add(handler);
            }
            //thread che mi resta sempre attivo e che legge i messaggi dal server al client
            new Thread(() -> {
                try {
                    handler.runVirtualView();
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
