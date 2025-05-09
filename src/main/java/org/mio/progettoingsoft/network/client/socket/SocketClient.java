package org.mio.progettoingsoft.network.client.socket;

import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.server.socket.VirtualClientSocket;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SocketClient implements Client, VirtualClientSocket {
    private final Socket socket;
    private final SocketServerHandler serverHandler;
    private final ObjectInputStream input;
    private final BlockingQueue<Message> inputMessageQueue;

    public SocketClient(Socket socket, BlockingQueue<Message> inputMessageQueue) throws IOException {
        this.socket = socket;
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        this.serverHandler = new SocketServerHandler(out);
        this.input = new ObjectInputStream(socket.getInputStream());
        this.inputMessageQueue = inputMessageQueue;
    }

    /**
     * METHODS INHERITED FROM Client INTERFACE
     */

    @Override
    public void run() {
        Thread listenerThread = new Thread(() -> {
            try {
                listenToServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, "server-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    @Override
    public void sendToServer(Message message) throws IOException {
        serverHandler.sendToServer(message);
    }

    @Override
    public void close() throws IOException {
        input.close();
        serverHandler.close();
        socket.close();
    }

    /**
     * METODI EREDITATI DALL'INTERFACCIA VirtualClient, essi vengono chiamati dal server indirettamente tramite il SocketClientHandler
     */

    @Override
    public synchronized void showUpdate(Message message) {
        inputMessageQueue.add(message);
    }

    /**
     * METODI SPECIFICI DI SocketClient
     */

    private void listenToServer() throws IOException, ClassNotFoundException {
        while (true) {
            // Sempre in ascolto, quando riceve qualcosa lo aggiunge alla coda
            showUpdate((Message) input.readObject());
        }
    }
}
