package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.network.VirtualView;

public final class JoinedGameMessage extends Message {
    public JoinedGameMessage(VirtualView client, String nickname) {
        super(client, nickname);
    }
}
