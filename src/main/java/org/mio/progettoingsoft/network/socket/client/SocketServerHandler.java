package org.mio.progettoingsoft.network.socket.client;

import org.mio.progettoingsoft.network.message.Message;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

/* Questa classe implementa la logica di interazione tra il client e il server.
 */

public class SocketServerHandler implements VirtualServerSocket {
    final ObjectOutputStream output;

    public SocketServerHandler(ObjectOutputStream output) {
        this.output = output;
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
    public void sendToServer(Message message) throws RemoteException {
    }
}
