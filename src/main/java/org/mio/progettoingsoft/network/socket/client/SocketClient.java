package org.mio.progettoingsoft.network.socket.client;

import org.mio.progettoingsoft.network.socket.server.VirtualViewSocket;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

public class SocketClient implements VirtualViewSocket {
    final BufferedReader input;
    final SocketServerHandler output;
    final Object screenLock = new Object();

    protected SocketClient(BufferedReader input, BufferedWriter output) {
        this.input = input;
        this.output = new SocketServerHandler(output);
    }

    private void run() {
        new Thread(() -> {
            try {
                runVirtualServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        runCli();
    }

    // comunicazione dal server al client
    private void runVirtualServer() throws IOException {
        String line;
        while ((line = input.readLine()) != null) {
            // Da notare che i metodi sono chiamati nello stesso modo del server
            switch (line) {
                case "update" -> this.showUpdate(Integer.parseInt(input.readLine()));
                case "error" -> this.reportError(input.readLine());
                default -> System.err.println("[INVALID MESSAGE]");
            }
        }
    }

    private void runCli()  {
        Scanner scan = new Scanner(System.in);
        //aspetto che inserisca il nome per loggarlo nella partita
        System.out.println("Insert nickname");
        String nickname = scan.next();
        this.output.newPlayer(nickname);
        while (true) {
            System.out.print("> ");
            int command = scan.nextInt();

            if (command == 0) {
                this.output.reset();
            } else {
                this.output.add(command);
            }
        }
    }

    public void showUpdate(Integer number) {
        // TODO. Attenzione, questo può causare data race con il thread dell'interfaccia o un altro thread!
        //per evitare data race faccio lock su screenLock un oggetto creato appositamente per non avere problemi
        //quando vado a mostrare gli aggiornamenti nella finestra di interazione con l'utente
        synchronized(screenLock) {
            System.out.print("\n= " + number + "\n> ");
        }
    }


    public void reportError(String details) throws RemoteException {
        synchronized(screenLock) {
            // TODO. Attenzione, questo può causare data race con il thread dell'interfaccia o un altro thread!
            System.err.print("\n[ERROR] " + details + "\n> ");
        }
    }

    public static void main(String[] args) throws IOException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        Socket serverSocket = new Socket(host, port);

        InputStreamReader socketRx = new InputStreamReader(serverSocket.getInputStream());
        OutputStreamWriter socketTx = new OutputStreamWriter(serverSocket.getOutputStream());

        new SocketClient(new BufferedReader(socketRx), new BufferedWriter(socketTx)).run();
    }

    @Override
    public void requestGameSetup() throws RemoteException {

    }

    @Override
    public void requestNickname() throws RemoteException {

    }

    @Override
    public void notify(String message) throws RemoteException {

    }
}
