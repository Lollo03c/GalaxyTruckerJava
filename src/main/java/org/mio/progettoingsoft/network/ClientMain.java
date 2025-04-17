package org.mio.progettoingsoft.network;

import java.io.IOException;
import java.rmi.NotBoundException;

public class ClientMain {
    public static void main(String[] args) throws NotBoundException, IOException {
        /* Questa classe serve per lancia ClientApp in modalità CLI o GUI a seconda dell'args in ingresso,
         * quindi deve occuparti SOLO di lancia ClientApp con il metodo run() e interfacciarsi con il logger*/

        System.out.println("Galaxy Truckers | 1.0 | AM34 | Client");

        boolean isGui = false;

        ClientApp clientApp = new ClientApp(isGui);
        clientApp.run();

    }
}
