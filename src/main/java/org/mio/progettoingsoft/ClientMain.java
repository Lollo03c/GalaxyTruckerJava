package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.client.ClientApp;
import org.mio.progettoingsoft.utils.ConnectionInfo;
import org.mio.progettoingsoft.utils.IPValidator;

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
        boolean isGui;

        /* SCELTA UI IN FASE DI LANCIO */
        /*
        if (argsList.contains("--cli"))
            isGui = false;
        else if (argsList.contains("--gui"))
            isGui = true;
        else {
            System.out.println("Start mode not specified");
        }*/

        /*SCELTA UI DOPO IL LANCIO*/
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
        String ip = "localhost"; /* or ip = args[0] */
        int socketPort = 1050, rmiPort = 1099;
        String serverName = "GameSpace";
        if(IPValidator.isIPValid(ip)) {
            ConnectionInfo connectionInfo = new ConnectionInfo(ip, socketPort, rmiPort, serverName);
            ClientApp clientApp = new ClientApp(isGui, connectionInfo);
            clientApp.run();
        }else{
            System.out.println("IP not valid");
        }
    }
}

