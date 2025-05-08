package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.ErrorType;
import org.mio.progettoingsoft.network.message.Message;

/**
 * Interfaccia che definisce i metodi utilizzati dal server per notificare i cambiamenti di stato ai client.
 */
public interface VirtualClient {
    void showUpdate(Message message);
    void reportError(int idGame, String nickname, ErrorType errorType) throws Exception;
}
