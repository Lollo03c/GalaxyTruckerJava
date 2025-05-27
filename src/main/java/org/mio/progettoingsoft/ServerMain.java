package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.server.*;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.IPValidator;
import org.mio.progettoingsoft.utils.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class ServerMain {
    public static void main(String[] args) throws RemoteException {
        System.out.println("[Galaxy Truckers | Server]\nServer starting...");

        String ip;
        if(args.length > 0) {
            ip = args[0];

            if (!IPValidator.isIPValid(ip)) {
                Logger.error("Invalid IP!");
                return;
            }
        } else {
            ip = "127.0.0.1";
        }

        ConnectionInfo connectionInfo = new ConnectionInfo(ip);
        ServerApp serverApp = new ServerApp(connectionInfo);
        serverApp.run();
    }
}
