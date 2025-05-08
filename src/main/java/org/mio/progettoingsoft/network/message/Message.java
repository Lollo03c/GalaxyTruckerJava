package org.mio.progettoingsoft.network.message;

import java.io.Serializable;

public abstract sealed class Message implements Serializable
        permits ErrorMessage, GameSetupMessage, JoinedGameMessage, NicknameMessage, WelcomeMessage {

    private final Integer idGame;
    private final String nickname;


    protected Message(Integer idGame, String nickname){
        this.idGame = idGame;
        this.nickname = nickname;
    }

    public int getIdGame(){
        return idGame;
    }

    public String getNickname() {
        return nickname;
    }
}