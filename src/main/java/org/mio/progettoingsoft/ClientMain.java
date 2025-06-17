package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.client.ClientApp;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.IPValidator;
import org.mio.progettoingsoft.utils.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMain {

    /**
     * how to launch client:
     * java -jar {nome-artifact} {IP-address}
     */
    public static void main(String[] args) {
        System.out.println("[Galaxy Truckers | Client]");

        boolean isGui;

        Scanner in = new Scanner(System.in);
        int choice = 0;
        while(choice != 1 && choice != 2) {
            System.out.println("1 : GUI\n2 : TUI\nMake your choice: ");
            try {
                choice = in.nextInt();
            } catch (Exception e) {
                choice = 0;
                in.nextLine();
            }
        }
        isGui = choice == 1;

        String ip;
        if(args.length > 0) {
            ip = args[0];
            while (!IPValidator.isIPValid(ip)) {
                System.out.print("Invalid IP! Please try again: ");
                ip = in.nextLine();
            }
        } else {
            // default IP
            ip = "127.0.0.1";
        }

        System.out.println("IP del server: " + ip);

        ConnectionInfo connectionInfo = new ConnectionInfo(ip);
        ClientApp clientApp = new ClientApp(isGui, connectionInfo);
        clientApp.run();
    }
}

