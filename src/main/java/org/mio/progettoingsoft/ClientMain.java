package org.mio.progettoingsoft;

import org.mio.progettoingsoft.network.client.ClientApp;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMain {

    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        boolean isGui = true;

        if (argsList.contains("--cli"))
            isGui = false;
        else if (argsList.contains("--gui"))
            isGui = true;
        else {
            System.out.println("Start mode not specified");
        }



        ClientApp clientApp = new ClientApp(isGui);
        clientApp.run();
    }
}

