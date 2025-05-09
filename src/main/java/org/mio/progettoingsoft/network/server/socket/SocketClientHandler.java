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

    //comunicazione dal client al server
    //chiamata dal server nel momento della connessione
    public void runVirtualClient() throws Exception {

        serverController.addClientToAccept(this);

        String line;
        while (true) {
            try {
                recivedMessageQueue.add((Message) input.readObject());
                //serverController.handleInput2(this,mex);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client crashed - " + e.getMessage());
                break;
            }
        }

    }

    // comunicazione dal server al client
    //non va bene, bisogna serializzare il messaggio println manda solo testo non messaggi
    @Override
    public void showUpdate(Message message)  {
        //this.output.println("send");
        try{
            this.output.writeObject(message);
            this.output.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //this.output.println(message);
        //this.output.flush();
    }
}