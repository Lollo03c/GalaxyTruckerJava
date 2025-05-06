package org.mio.progettoingsoft.network.socket.client;

import org.mio.progettoingsoft.network.message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

/* Questa classe implementa la logica di interazione tra il client e il server.
 */

public class SocketServerHandler implements VirtualServerSocket {
    private final ObjectOutputStream out;

    public SocketServerHandler(ObjectOutputStream output) {
        this.out = output;
    }

    public void newPlayer(String nickname) {
        /*
        try{
            NewPlayerMessage newPlayerMessage = new NewPlayerMessage(nickname);
            this.output.writeObject(newPlayerMessage);
            this.output.flush();
        } catch (Exception e) {
            System.out.println("New player failed");
        }
         */
    }

    @Override
    public void sendToServer(Message message) throws IOException {
        out.writeObject(message);
        out.reset();
        out.flush();
        System.out.println("ho spedito il messaggio al server");
    }
}
