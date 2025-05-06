package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.rmi.client.RmiClient;
import org.mio.progettoingsoft.network.rmi.client.VirtualServerRmi;
import org.mio.progettoingsoft.network.socket.client.SocketClient;
import org.mio.progettoingsoft.views.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;

public class NetworkFactory {
    public static Client create(ConnectionType info, VirtualView view, BlockingQueue<Message> inputMessageQueue) throws IOException, NotBoundException {
        if (info.isRmi()) {
            Registry registry = LocateRegistry.getRegistry(info.getHost(), info.getPort());

            VirtualServerRmi server = (VirtualServerRmi) registry.lookup(info.getServerName());

            return new RmiClient(server, inputMessageQueue);
        } else {
            Socket serverSocket = new Socket(info.getHost(), info.getPort());
            ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
            return new SocketClient(in, out, inputMessageQueue);
        }
    }
}
