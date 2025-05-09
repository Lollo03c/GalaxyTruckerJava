package org.mio.progettoingsoft.network.client.socket;

import org.mio.progettoingsoft.network.server.VirtualServer;
import org.mio.progettoingsoft.network.message.Message;

import java.io.IOException;

/**
 * Questa interfaccia specializza l'interfaccia VirtualServer per la tecnologia Socket
 */
public interface VirtualServerSocket extends VirtualServer {
    @Override
    void sendToServer(Message message) throws IOException;
}
