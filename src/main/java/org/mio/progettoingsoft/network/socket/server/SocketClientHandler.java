package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.message.ErrorMessage;
import org.mio.progettoingsoft.network.message.ErrorType;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.message.WelcomeMessage;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SocketClientHandler implements VirtualClientSocket {
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final BlockingQueue<Message> recivedMessageQueue;

    public SocketClientHandler(Socket socket, BlockingQueue<Message> recivedMessageQueue) {
        try {
            this.input = new ObjectInputStream(socket.getInputStream());
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {

        }
        this.recivedMessageQueue = recivedMessageQueue;
    }

    //comunicazione dal client al server
    //chiamata dal server nel momento della connessione
    public void runVirtualClient() throws IOException {
        int idClient = GameManager.getInstance().getNextIdPlayer();
        showUpdate(new WelcomeMessage(idClient));


        GameManager.getInstance().addClientToAccept(idClient, this);

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
    public void reportError(int idGame, String nickname, ErrorType errorType) throws Exception{
        showUpdate(new ErrorMessage(idGame, nickname, errorType));
    }
}