package org.mio.progettoingsoft.network.client.socket;

import org.mio.progettoingsoft.network.message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

/* Questa classe implementa la logica di interazione tra il client e il server.
 */

public class SocketServerHandler implements VirtualServerSocket {
    private final ObjectOutputStream output;

    public SocketServerHandler(ObjectOutputStream output) {
        this.output = output;
    }

    @Override
    public void sendToServer(Message message) throws IOException {
        output.writeObject(message);
        output.reset();
        output.flush();
        System.out.println("Il messaggio è stato spedito.");
    }

    public void close() throws IOException {
        this.output.close();
    }
}
