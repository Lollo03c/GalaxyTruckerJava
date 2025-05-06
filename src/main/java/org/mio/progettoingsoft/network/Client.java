package org.mio.progettoingsoft.network;

import org.mio.progettoingsoft.network.message.Message;

public interface Client {
    void run();

    //manda un messaggio dal client al server
    void sendInput(Message message) throws Exception;

    void close();
}
