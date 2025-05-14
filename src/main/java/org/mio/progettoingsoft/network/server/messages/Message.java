package org.mio.progettoingsoft.network.server.messages;

import java.io.Serializable;

public abstract sealed class Message implements Serializable permits GameInfoMessage, SetNicknameMessage, WelcomeMessage {
    private final int idGame;
    private final String nickname;

    public Message(int idGame, String nickname) {
        this.idGame = idGame;
        this.nickname = nickname;
    }

    public int getIdGame() {
        return idGame;
    }

    public String getNickname() {
        return nickname;
    }
}
