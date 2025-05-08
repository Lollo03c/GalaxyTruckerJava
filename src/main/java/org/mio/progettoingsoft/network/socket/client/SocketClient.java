package org.mio.progettoingsoft.network.socket.client;

import org.mio.progettoingsoft.network.Client;
import org.mio.progettoingsoft.network.ClientController;
import org.mio.progettoingsoft.network.message.ErrorMessage;
import org.mio.progettoingsoft.network.message.ErrorType;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.socket.server.VirtualClientSocket;

import java.io.*;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

public class SocketClient implements Client, VirtualClientSocket {
    private final SocketServerHandler output;
    private final ObjectInputStream in;
    private final ClientController clientController;
    private final BlockingQueue<Message> inputMessageQueue;

    final Object screenLock = new Object();

    public SocketClient(ObjectInputStream in, ObjectOutputStream out, BlockingQueue<Message> messageQueue) throws IOException {
        this.output = new SocketServerHandler(out);
        this.in = in;
        this.inputMessageQueue = messageQueue;
        this.clientController = ClientController.get();
    }

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
    public void sendInput(Message message) throws IOException {
        output.sendToServer(message);
    }

    @Override
    public void sendError(int idGame, String nickname, ErrorType errorType) throws IOException {
        output.sendToServer(new ErrorMessage(idGame, nickname, errorType));
    }

    @Override
    public void close() {

    }

    @Override
    public synchronized void showUpdate(Message message) {
        inputMessageQueue.add(message);
    }

    @Override
    public synchronized void reportError(int idGame, String nickname, ErrorType errorType) throws Exception{
        showUpdate(new ErrorMessage(idGame, nickname, errorType));
    }

    private void listenToServer() throws IOException, ClassNotFoundException {
        while (true) {
            // Sempre in ascolto, quando riceve qualcosa lo aggiunge alla coda
            showUpdate(receive());
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    public void reportError(String details) {
        synchronized(screenLock) {
            // TODO. Attenzione, questo può causare data race con il thread dell'interfaccia o un altro thread!
            System.err.print("\n[ERROR] " + details + "\n> ");
        }
    }
}
