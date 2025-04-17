package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.network.VirtualClient;

public final class RequestSetupMessage extends Message {
    public RequestSetupMessage(VirtualClient client, String nickname) {
        super( client, nickname);
    }
    public RequestSetupMessage(){}
}