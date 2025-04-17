package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.network.VirtualClient;

import java.io.Serializable;

public sealed class Message implements Serializable
        permits RequestSetupMessage, JoinedGameMessage, GameSetupInput{
    private VirtualClient client;
    private String nickname;

    public Message(VirtualClient client , String nickname) {
        this.client = client;
        this.nickname = nickname;
    }
    public Message() {}

    public VirtualClient getClient() {     return client;    }

    public String getNickname() {
        return nickname;
    }
}