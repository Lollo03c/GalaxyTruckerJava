package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.ClientController;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.server.VirtualViewRmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RmiClient extends UnicastRemoteObject implements VirtualViewRmi {
    final VirtualServerRmi server;
    final ClientController clientController;
    private Queue<Message> serverMessageQueue;

    public  RmiClient(VirtualServerRmi server) throws RemoteException {
        super();
        this.server = server;
        this.clientController = new ClientController(server);
        this.serverMessageQueue = new ConcurrentLinkedQueue<>();
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "localhost";
        final int serverPort = 1234;

        Registry registry = LocateRegistry.getRegistry("127.0.0.1", serverPort);

        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        new RmiClient(server).run();
    }

    private void run() throws RemoteException {
        Scanner scan = new Scanner(System.in);

        System.out.print("Per poterti connettere al server inserisci un nickname: ");
        String nickname = scan.nextLine();

        this.server.connect(this, nickname);

        startMessageProcessor();
    }

    private void startMessageProcessor() {
        Thread processor = new Thread(() -> {
            while (true) {
                Message message = serverMessageQueue.poll();

                if (message != null) {
                    try {
                        clientController.handleMessage(message);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        processor.setDaemon(true); // Imposta il thread come daemon, così verrà terminato quando il programma finisce
        processor.start();
    }


    @Override
    public void update(Message message) {
        serverMessageQueue.add(message);
    }

    @Override
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void notify(String message) throws RemoteException {
        System.out.print(message + "\n");
    }
}
