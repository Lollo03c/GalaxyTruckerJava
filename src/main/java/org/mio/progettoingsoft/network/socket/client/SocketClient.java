package org.mio.progettoingsoft.network.socket.client;

import org.mio.progettoingsoft.network.ClientController;
import org.mio.progettoingsoft.network.SerMessage.SerMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.socket.server.VirtualViewSocket;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketClient implements VirtualViewSocket {
    final SocketServerHandler output;
    final Object screenLock = new Object();
    final private Queue<SerMessage> serverMessageQueue2;
    final ClientController clientController;
    ObjectInputStream inputObject;




    protected SocketClient(Socket socket) throws IOException {
        this.output = new SocketServerHandler(new ObjectOutputStream(socket.getOutputStream()));
        this.serverMessageQueue2 = new ConcurrentLinkedQueue<>();
        this.clientController = new ClientController(this.output);
        inputObject = new ObjectInputStream(socket.getInputStream());
    }

    private void run() {
        new Thread(() -> {
            try {
                runVirtualServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        runCli();
    }

    private void runVirtualServer() {
        String line;
        while (true){
            try{
                SerMessage message = (SerMessage) inputObject.readObject();
                if(message != null ){
                    synchronized (screenLock){
                        //serverMessageQueue.add(SerMessage);
                        clientController.handleMessage2(this,message);
                    }
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    //thread che rimane in ascolto della CLI del client
    private void runCli()  {
        Scanner scan = new Scanner(System.in);
        //aspetto che inserisca il nome per loggarlo nella partita
        System.out.println("Insert nickname");
        String nickname = scan.next();
        this.output.newPlayer(nickname);
        //da modificare questa parte !!!!
        while (true) {
            System.out.print("> ");
            int command = scan.nextInt();

            if (command == 0) {
                this.output.reset();
            } else {
                this.output.add(command);
            }
        }
    }

    public void update(Message message) {
        synchronized (screenLock) {
            //serverMessageQueue.add(message);
        }
    }


    public void reportError(String details) throws RemoteException {
        synchronized(screenLock) {
            // TODO. Attenzione, questo può causare data race con il thread dell'interfaccia o un altro thread!
            System.err.print("\n[ERROR] " + details + "\n> ");
        }
    }

    @Override
    public void update2(SerMessage message) throws RemoteException {

    }

    public static void main(String[] args) throws IOException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket serverSocket = new Socket(host, port);
        new SocketClient(serverSocket).run();
    }

    @Override
    public void notify(String message) throws RemoteException {

    }
}
