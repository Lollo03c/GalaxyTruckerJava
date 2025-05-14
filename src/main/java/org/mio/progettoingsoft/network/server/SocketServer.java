package org.mio.progettoingsoft.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class SocketServer implements Server {
    private final int port = 1050;

    public SocketServer(){
    }

    @Override
    public void run(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server socket on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("new socket client " + clientSocket);

                SocketClientHandler clientHandler = new SocketClientHandler(clientSocket);
                new Thread(clientHandler).start();



            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
