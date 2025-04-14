package org.mio.progettoingsoft.network.SerMessage;

import java.io.Serializable;

public final class NewPlayerMessage extends SerMessage {
    private String nickname;
    public NewPlayerMessage(String nickname){
        super(nickname);
    }
}
