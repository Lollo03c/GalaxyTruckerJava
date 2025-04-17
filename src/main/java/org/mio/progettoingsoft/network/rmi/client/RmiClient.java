package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.Client;
import org.mio.progettoingsoft.network.MessageHandler;
import org.mio.progettoingsoft.network.SerMessage.SerMessage;
import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.views.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RmiClient extends UnicastRemoteObject implements VirtualClient, Client {
    final VirtualServerRmi server;
    final MessageHandler messageHandler;
    private BlockingQueue<Message> serverMessageQueue;

    private final VirtualView view;

    public RmiClient(VirtualServerRmi server, VirtualView view, BlockingQueue<Message> serverMessageQueue) throws RemoteException {
        super();
        this.server = server;
        this.messageHandler = new MessageHandler(server);
        this.serverMessageQueue = serverMessageQueue;

        this.view = view;
    }

    @Override
    public void run() throws RemoteException {
        String nickname = view.askNickname();

        startMessageProcessor();

        this.server.connect(this, nickname);
    }

    @Override
    public void close(){

    }

    private void startMessageProcessor() {
        Thread processor = new Thread(() -> {
            while (true) {
                Message message = serverMessageQueue.poll();

                if (message != null) {
                    try {
                        messageHandler.handleMessage(message);
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
    public void update2(SerMessage message) throws RemoteException{

    }

    @Override
    public void update(Message message) {
        serverMessageQueue.add(message);
    }

    @Override
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void notify(String message) throws RemoteException{

    }

}
