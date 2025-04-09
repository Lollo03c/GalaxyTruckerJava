package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.network.VirtualView;

public final class RequestSetupMessage extends Message {
    public RequestSetupMessage(VirtualView client, String nickname) {
        super(client, nickname);
    }
}
