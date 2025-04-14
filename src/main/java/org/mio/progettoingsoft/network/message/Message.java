package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.network.VirtualView;

import java.io.Serializable;

public sealed class Message implements Serializable
        permits RequestSetupMessage, JoinedGameMessage, GameSetupInput{
    private VirtualView client;
    private String nickname;

    public Message(VirtualView client , String nickname) {
        this.client = client;
        this.nickname = nickname;
    }
    public Message() {}

    public VirtualView getClient() {     return client;    }

    public String getNickname() {
        return nickname;
    }
}
