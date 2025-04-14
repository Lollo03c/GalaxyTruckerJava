package org.mio.progettoingsoft.network.SerMessage;

import java.io.Serializable;

// sealed class: solo certe sottoclassi possono estendere
public sealed class SerMessage implements Serializable
        permits NewPlayerMessage, RequestSetupMessage2, JoinedGameMessage2, GameSetupInput2 {

    private final String nickname;

    public SerMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }
}
