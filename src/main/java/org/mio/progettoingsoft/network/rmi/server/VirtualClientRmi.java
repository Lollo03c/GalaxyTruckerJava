package org.mio.progettoingsoft.network.rmi.server;

import org.mio.progettoingsoft.network.VirtualClient;
import org.mio.progettoingsoft.network.message.ErrorType;
import org.mio.progettoingsoft.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClientRmi extends Remote, VirtualClient {
    @Override
    void showUpdate(Message message);

    @Override
    void reportError(int idGame, String nickname, ErrorType errorType) throws Exception;
}
