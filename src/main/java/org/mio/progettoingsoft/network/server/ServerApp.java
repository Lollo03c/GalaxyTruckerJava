package org.mio.progettoingsoft.network.server;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp implements Runnable {
    ExecutorService executor = Executors.newFixedThreadPool(6);

    @Override
    public void run(){
        try {
            Server serverRmi = new ServerRmi();
            serverRmi.run();
            System.out.println("Rmi Server started wth port 1099 with registry 'GameSpace'");

            SocketServer serverSocket = new SocketServer();
            new Thread(serverSocket::run).start();
            System.out.println("Socket server started with port 1050");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
