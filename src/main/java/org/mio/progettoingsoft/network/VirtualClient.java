package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;

/**
 * Interfaccia che definisce i metodi utilizzati dal server per notificare i cambiamenti di stato ai client.
 */
public interface VirtualClient {
    void sendToClient(Message message) throws Exception;
    void reportError(String details) throws Exception;
}
