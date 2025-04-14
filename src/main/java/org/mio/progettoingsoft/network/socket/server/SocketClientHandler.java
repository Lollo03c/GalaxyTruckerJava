package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.GameController;
import org.mio.progettoingsoft.network.message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

public class SocketClientHandler implements VirtualViewSocket {

    final GameController controller;
    final SocketServer server;
    final BufferedReader input; //canale da cui leggo ciò che mi invia il client
    final PrintWriter output; //canale da cui scrivo ciò che voglio inviare al client

    public SocketClientHandler(GameController controller, SocketServer server, BufferedReader input, PrintWriter output) {
        this.controller = controller;
        this.server = server;
        this.input = input;
        this.output = output;
    }

    //comunicazione dal client al server
    public void runVirtualView() throws IOException {
        String line;

        //TODO. Attenzione, qui non sto sfruttando il paradigma object oriented!
        while ((line = input.readLine()) != null) {
            // Reflection
            // Protocollo di serializzazione
            switch (line) {
                //implementerei prima caso di game singolo e poi penseremo al resto
                case "newPlayer" -> {
                    String nickname = input.readLine();
                    System.err.print("New player: " + nickname );
                    //this.controller.addPlayer(new Game(4,nickname), nickname);
                }
                case "add" -> {
                    System.err.println("add request received");

                    //this.controller.add(Integer.parseInt(input.readLine()));
                    //this.server.broadcastUpdate(this.controller.getCurrent());
                }
                case "reset" -> {
                    System.err.println("reset request received");

                    /*boolean result = this.controller.reset();
                    if (result) {
                        this.server.broadcastUpdate(this.controller.getCurrent());
                    } else {
                        this.server.broadcastError();
                    }
                }*/
                    //default -> System.err.println("[INVALID MESSAGE]");
                }
            }
        }
    }

    // comunicazione dal server al client
    @Override
    public void update(Message message)  {
        this.output.println("update");
        this.output.println(message);
        this.output.flush();
    }

    @Override
    public void reportError(String details) throws RemoteException {
        this.output.println("error");
        this.output.println(details);
        this.output.flush();
    }

    @Override
    public void notify(String message) throws RemoteException {

    }
}