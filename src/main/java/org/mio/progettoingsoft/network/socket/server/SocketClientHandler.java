package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.network.message.Message;

import java.io.*;
import java.util.concurrent.BlockingQueue;

public class SocketClientHandler implements VirtualClientSocket {
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final BlockingQueue<Message> recivedMessageQueue;

    public SocketClientHandler(ObjectInputStream in, ObjectOutputStream out, BlockingQueue<Message> recivedMessageQueue) {
        this.input = in;
        this.output = out;
        this.recivedMessageQueue = recivedMessageQueue;
    }

    //comunicazione dal client al server
    //chiamata dal server nel momento della connessione
    public void runVirtualClient() throws IOException {
        String line;
        while (true) {
            try {
                recivedMessageQueue.add((Message) input.readObject());
                System.out.println("ho ricevuto il messaggio da ");
                //serverController.handleInput2(this,mex);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
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

    @Override
    public void reportError(String details) {
        //this.output.println("error");
        //this.output.println(details);
        //this.output.flush();
    }
}