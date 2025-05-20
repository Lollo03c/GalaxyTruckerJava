package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.messages.*;
import org.mio.progettoingsoft.network.server.VirtualServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class VirtualSocketServer implements VirtualServer {
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public VirtualSocketServer(ObjectInputStream in, ObjectOutputStream out){
        this.outputStream = out;
        this.inputStream = in;
    }

    private void sendMessage(Message message){
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            outputStream.reset();
        }
        catch (IOException e){

        }
    }

}
