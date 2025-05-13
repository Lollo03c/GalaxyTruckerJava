package org.mio.progettoingsoft.network.server.socket;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.WelcomeMessage;
import org.mio.progettoingsoft.network.server.ServerController;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SocketClientHandler implements VirtualClientSocket {
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final BlockingQueue<Message> recivedMessageQueue;
    private final ServerController serverController;

    public SocketClientHandler(Socket socket, BlockingQueue<Message> recivedMessageQueue) {
        try {
            this.input = new ObjectInputStream(socket.getInputStream());
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {

        }
        this.recivedMessageQueue = recivedMessageQueue;
        serverController = ServerController.getInstance();
    }

    /**
     * called by a new thread when a new client connects to the server
     * the thread waits for messages from the client, if a message arrives it is added to the queue
     */

    public void runVirtualClient() throws Exception {

        serverController.addClientToAccept(this);

        String line;
        while (true) {
            try {
                recivedMessageQueue.add((Message) input.readObject());
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client crashed - " + e.getMessage());
                break;
            }
        }

    }

    /**
     * this method is used to send a {@link Message} to the client
     * @param message: the message to send to the client (it can be a Welcome or un update of the state)
     */
    @Override
    public void showUpdate(Message message)  {
        //this.output.println("send");
        try{
            this.output.writeObject(message);
            this.output.flush();
        }
        catch (IOException e) {
            System.out.println("Error while sending a message to" + message.getNickname());
        }
    }
}