package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.GameController;
import org.mio.progettoingsoft.network.ServerController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {

    final ServerSocket listenSocket;
    final ServerController controller;
    final List<SocketClientHandler> clients = new ArrayList<>();

    public SocketServer(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
        this.controller = new ServerController();
    }

    private void runServer() throws IOException {
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
        synchronized (this.clients) {
            for (var client : this.clients) {
                //client.update(value);
            }
        }
    }

    public void broadcastError() {
        synchronized (this.clients) {
            for (VirtualViewSocket client : clients) {
                try {
                    client.reportError("already reset");
                } catch (java.rmi.RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        ServerSocket listenSocket = new ServerSocket(port);
        System.out.println("Listening on " + host + " : " + port);
        new SocketServer(listenSocket).runServer();
    }
}
