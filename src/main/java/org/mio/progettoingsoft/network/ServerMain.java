package org.mio.progettoingsoft.network;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        System.out.println("Galaxy Truckers | Server");

        ServerApp serverApp = new ServerApp();
        serverApp.run();
    }
}
