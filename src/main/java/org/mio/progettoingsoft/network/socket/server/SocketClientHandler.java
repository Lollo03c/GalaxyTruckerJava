package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.network.SerMessage.SerMessage;
import org.mio.progettoingsoft.network.ServerController;
import org.mio.progettoingsoft.network.message.Message;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;

public class SocketClientHandler implements VirtualClientSocket {

    final ServerController serverController;
    final SocketServer server;
    ObjectInputStream input; //canale da cui leggo ciò che mi invia il client
    //    final PrintWriter output; //canale da cui scrivo ciò che voglio inviare al client
    ObjectOutputStream output;
    Socket socket;

    public SocketClientHandler(ServerController controller, SocketServer server, Socket socket) {
        this.serverController = controller;
        this.server = server;
        try {
            this.input = new ObjectInputStream(socket.getInputStream());
            this.output = new ObjectOutputStream(socket.getOutputStream());

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.socket = socket;
    }

    //comunicazione dal client al server
    //chiamata dal server nel momento della connessione

    public void runVirtualView() throws IOException {
        String line;
        while (true) {
            try {
                SerMessage mex = (SerMessage) input.readObject();
                serverController.handleInput2(this,mex);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }

    }

    // comunicazione dal server al client
    //non va bene, bisogna serializzare il messaggio println manda solo testo non messaggi
    @Override
    public void update(Message message)  {
        //this.output.println("update");
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
    public void reportError(String details) throws RemoteException {
        //this.output.println("error");
        //this.output.println(details);
        //this.output.flush();
    }

    @Override
    public void update2(SerMessage message) throws RemoteException {
        try{
            this.output.writeObject(message);
            this.output.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(String message) throws RemoteException {

    }
}