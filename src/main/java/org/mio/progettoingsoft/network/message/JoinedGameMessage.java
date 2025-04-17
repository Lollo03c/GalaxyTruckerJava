package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.network.VirtualClient;

public final class JoinedGameMessage extends Message {
    public JoinedGameMessage(VirtualClient client , String nickname) {
        super(client, nickname);
    }
    public JoinedGameMessage() {}
}