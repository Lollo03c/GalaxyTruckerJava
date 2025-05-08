package org.mio.progettoingsoft.network.client;

import java.io.IOException;
import java.rmi.NotBoundException;

public class ClientMain {
    public static void main(String[] args) throws NotBoundException, IOException {
        /* Questa classe serve per lancia ClientApp in modalità CLI o GUI a seconda dell'args in ingresso,
         * quindi deve occuparti SOLO di lancia ClientApp con il metodo run() e interfacciarsi con il logger*/

        System.out.println("Galaxy Truckers | Client");

        boolean isGui = true;

        // Arguments parsing
        for (String arg : args) {
            switch (arg) {
                case "-t", "--tui" -> isGui = false;
                // TODO: gestire in altro modo, stampando su terminale il motivo del problema
                default -> throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        ClientApp clientApp = new ClientApp(isGui);
        clientApp.run();
    }
}
