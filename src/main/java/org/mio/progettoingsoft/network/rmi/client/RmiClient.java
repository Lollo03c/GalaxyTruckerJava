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
        // Richiesta di connessione del client al server
        this.server.connect(this);

        // Richiesta di entrare nel gioco: il client viene messo in una partita nuova o in una in attesa
        this.server.join(this);
    }

    @Override
    public void requestGameSetup() throws RemoteException {
        Scanner scan = new Scanner(System.in);

        System.out.print("Non ci sono partite esistenti.\nCreazione di una partita in corso ...\nInserisci nickname: ");
        String nickname = scan.nextLine();

        System.out.print("Quanti giocatori partecipano alla partita? ");
        int numPlayers = scan.nextInt();

        server.createGame(this, nickname, numPlayers);
    }

    @Override
    public void requestNickname() throws RemoteException {
        Scanner scan = new Scanner(System.in);

        System.out.print("Inserisci nickname: ");
        String nickname = scan.nextLine();

        server.joinGame(this, nickname);
    }

    @Override
    public void notify(String message) throws RemoteException {
        System.out.print(message + "\n");
    }

    @Override
    public void showUpdate(Integer number) {

    }

    @Override
    public void reportError(String details) throws RemoteException {

    }
}
