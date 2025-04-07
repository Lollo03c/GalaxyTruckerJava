package org.mio.progettoingsoft.network.rmi.client;

import org.mio.progettoingsoft.network.rmi.server.VirtualViewRmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RmiClient extends UnicastRemoteObject implements VirtualViewRmi {
    final VirtualServerRmi server;

    public  RmiClient(VirtualServerRmi server) throws RemoteException {
        super();
        this.server = server;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "localhost";
        final int serverPort = 1234;

        Registry registry = LocateRegistry.getRegistry("127.0.0.1", serverPort);

        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        new RmiClient(server).run();
    }

    private void run() throws RemoteException {
        this.server.connect(this);

        this.runCli();
    }

    private void runCli() throws RemoteException {
        System.out.println("Connesso al server.");

        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("Inserisci nickname: ");
            String nickname = scan.nextLine();
            server.join(nickname, this);
        }
    }
}
