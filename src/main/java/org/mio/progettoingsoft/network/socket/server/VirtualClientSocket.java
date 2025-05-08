package org.mio.progettoingsoft.network.socket.server;

import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.ErrorType;
import org.mio.progettoingsoft.network.message.Message;

import java.io.IOException;

/**
 * Questa interfaccia specializza l'interfaccia VirtualView per la tecnologia Socket
 */
public interface VirtualClientSocket extends VirtualClient {
    @Override
    void showUpdate(Message message);

    @Override
    void reportError(int idGame, String nickname, ErrorType errorType) throws Exception;
}
