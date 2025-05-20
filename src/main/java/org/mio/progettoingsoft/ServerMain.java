package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.server.*;
import org.mio.progettoingsoft.utils.ConnectionInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class ServerMain {

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        /* used to set the ip as the local ip (not localhost but 192.168.x.x) */
        /*try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }*/
        String serverName = "GameSpace";
        int socketPort = 1050, rmiPort = 1099;
        ConnectionInfo connectionInfo = new ConnectionInfo(ip, socketPort, rmiPort, serverName);
        ServerApp serverApp = new ServerApp(connectionInfo);
        serverApp.run();
    }
}
