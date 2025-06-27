package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.IPValidator;

import java.util.Scanner;

/**
 * The main class for launching the client application of Galaxy Truckers.
 * This class handles initial user interaction for selecting the user interface (GUI or TUI)
 * and validating the server's IP address.
 */
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
            System.out.print("1 : GUI\n2 : TUI\nMake your choice: ");
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

