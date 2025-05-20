package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.client.ClientSocket;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.client.VirtualSocketClient;
import org.mio.progettoingsoft.network.messages.Message;
import org.mio.progettoingsoft.network.messages.WelcomeMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

public class SocketClientHandler extends Server implements Runnable {
    private final Socket clientSocket;
    private final BlockingQueue<Message> receivedMessages;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public SocketClientHandler(Socket socket, BlockingQueue<Message> receivedMessages) throws RemoteException {
        this.clientSocket = socket;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void run(){
        try{
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            WelcomeMessage welcomeMessage = (WelcomeMessage) in.readObject();
            VirtualClient virtualClient = new VirtualSocketClient(in, out);

            int idClient = GameManager.getInstance().addClientToAccept(virtualClient);
            out.writeObject(new WelcomeMessage(-1, "", idClient));
            out.flush();
            out.reset();


            Object receivedObject;
            try {
                while ((receivedObject = in.readObject()) != null) {
                    if (receivedObject instanceof Message message)
                        receivedMessages.put(message);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int registerClient(VirtualClient client){
        return -1;
    }
}
