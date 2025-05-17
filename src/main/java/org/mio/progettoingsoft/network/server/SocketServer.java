package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;

import java.rmi.RemoteException;

public class SocketServer extends Server{

    @Override
    public int registerClient(VirtualClient client){
        return -1;
    }

}
