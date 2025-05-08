package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.ErrorType;
import org.mio.progettoingsoft.network.message.Message;

import java.io.IOException;
import java.rmi.RemoteException;

public interface Client {
    void run();

    //manda un messaggio dal client al server
    void sendInput(Message message) throws Exception;
    void sendError(int idGame, String nickname, ErrorType errorType) throws IOException;
    void close();
}
