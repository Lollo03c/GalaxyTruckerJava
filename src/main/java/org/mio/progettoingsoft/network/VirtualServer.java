package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;

/**
 * Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con
 * la logica del server (controller).
 */
public interface VirtualServer {
    void sendToServer(Message message) throws Exception;
}
