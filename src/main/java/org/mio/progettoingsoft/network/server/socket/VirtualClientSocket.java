package org.mio.progettoingsoft.network.server.socket;

import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.message.Message;

/**
 * Questa interfaccia specializza l'interfaccia VirtualView per la tecnologia Socket
 */
public interface VirtualClientSocket extends VirtualClient {
    @Override
    void showUpdate(Message message);
}
