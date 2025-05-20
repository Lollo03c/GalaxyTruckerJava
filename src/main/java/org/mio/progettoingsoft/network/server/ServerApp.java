package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.network.messages.Message;
import org.mio.progettoingsoft.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerApp implements Runnable {

    @Override
    public void run(){
        startRmiServer();
        startSocketServer();
    }

    private void startRmiServer(){
        final int portRmi = 1099;
        try {
            //192.168.1.147
            System.setProperty("java.rmi.server.hostname", "localhost");
            Registry registry =  LocateRegistry.createRegistry(portRmi);

            ServerRMI rmiServer = new ServerRMI();
            registry.rebind("GameSpace", rmiServer);

            Logger.info("Server RMI running on port " + portRmi);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void startSocketServer() {
        final int port = 1050;

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            Logger.info("Server Socket running on port " + port);

            Server socketServer = new SocketServer();

            BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();
            ServerMessageHandler serverMessageHandler = new ServerMessageHandler(socketServer, receivedMessages);
            new Thread(serverMessageHandler).start();

            while (true){
                Socket clientSocket = serverSocket.accept();

                SocketClientHandler clientHandler = new SocketClientHandler(clientSocket, receivedMessages);
                new Thread(clientHandler).start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
