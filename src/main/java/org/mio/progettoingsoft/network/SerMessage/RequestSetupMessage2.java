package org.mio.progettoingsoft.network.SerMessage;

import java.io.Serializable;

public final class RequestSetupMessage2 extends SerMessage {
    private String nickname;
    public RequestSetupMessage2(String nickname) {
        super(nickname);
    }

}
