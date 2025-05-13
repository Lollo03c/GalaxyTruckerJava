package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.client.Client;
import org.mio.progettoingsoft.network.message.Message;
import org.mio.progettoingsoft.network.client.rmi.RmiClient;
import org.mio.progettoingsoft.network.client.rmi.VirtualServerRmi;
import org.mio.progettoingsoft.network.client.socket.SocketClient;
import org.mio.progettoingsoft.views.VirtualView;

import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;

public class NetworkFactory {
    public static Client create(ConnectionType info, VirtualView view, BlockingQueue<Message> inputMessageQueue)  {
        try {
            if (info.isRmi()) {
                Registry registry = LocateRegistry.getRegistry(info.getHost(), info.getPort());

                VirtualServerRmi server = (VirtualServerRmi) registry.lookup(info.getServerName());

                return new RmiClient(server, inputMessageQueue);
            } else {
                Socket serverSocket = new Socket(info.getHost(), info.getPort());
                return new SocketClient(serverSocket, inputMessageQueue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
