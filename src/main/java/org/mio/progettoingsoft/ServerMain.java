package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.server.*;

import java.rmi.RemoteException;

public class ServerMain {

    public static void main(String[] args) {
        ServerApp serverApp = new ServerApp();
        serverApp.run();
    }
}
