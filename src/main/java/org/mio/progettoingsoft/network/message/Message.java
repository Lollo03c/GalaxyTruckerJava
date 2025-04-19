package org.mio.progettoingsoft.network.message;

import java.io.Serializable;

public sealed class Message implements Serializable
        permits NicknameMessage, JoinedGameMessage, GameSetupInput {
    private String nickname;

    public Message(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}