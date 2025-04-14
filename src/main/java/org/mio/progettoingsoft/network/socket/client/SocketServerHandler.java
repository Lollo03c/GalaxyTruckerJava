package org.mio.progettoingsoft.network.socket.client;

import org.mio.progettoingsoft.network.SerMessage.SerMessage;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.SerMessage.NewPlayerMessage;

import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

/* Questa classe implementa la logica di interazione tra il client e il server.
 */
public class SocketServerHandler implements VirtualServerSocket {
    final ObjectOutputStream output;

    public SocketServerHandler(ObjectOutputStream output) {
        this.output = output;
    }

    // comunicazione dal client al server
    @Override
    public void add(Integer number) {
    /*     output.println("add");
        output.println(number);
        output.flush();*/
    }

    @Override
    public void reset() {
        /*output.println("reset");
        output.flush();*/
    }
    public void newPlayer(String nickname) {
        try{
            NewPlayerMessage newPlayerMessage = new NewPlayerMessage(nickname);
            this.output.writeObject(newPlayerMessage);
            this.output.flush();
        } catch (Exception e) {
            System.out.println("New player failed");
        }
    }

    @Override
    public void sendInput(Message message) throws RemoteException {
    }
    @Override
    public void sendInput2(SerMessage message) throws RemoteException {
        try {
            this.output.writeObject(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
