package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.network.message.Message;

/**
 * Interfaccia che definisce i metodi utilizzati dal server per notificare i cambiamenti di stato ai client.
 */

public interface VirtualClient {
    // Manda messaggio dal server al client
    void showUpdate(Message message) throws Exception;
}