package org.mio.progettoingsoft.network.socket.client;

import org.mio.progettoingsoft.network.VirtualView;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.server.VirtualViewRmi;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

/* Questa classe implementa la logica di interazione tra il client e il server.
 */
public class SocketServerHandler implements VirtualServerSocket {
    final PrintWriter output;

    public SocketServerHandler(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    // comunicazione dal client al server
    @Override
    public void add(Integer number) {
        output.println("add");
        output.println(number);
        output.flush();
    }

    @Override
    public void reset() {
        output.println("reset");
        output.flush();
    }
    public void newPlayer(String nickname) {
        output.println("newPlayer");
        output.println(nickname);
        output.flush();
    }

    @Override
    public void sendInput(Message message) throws RemoteException {

    }

    @Override
    public void createGame(VirtualView client, String nickname, int numPlayers) throws RemoteException {

    }

    @Override
    public void joinGame(VirtualView client, String nickname) throws RemoteException {

    }
}
