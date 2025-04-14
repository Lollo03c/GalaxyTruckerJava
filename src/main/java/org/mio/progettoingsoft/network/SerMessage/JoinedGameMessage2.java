package org.mio.progettoingsoft.network.SerMessage;

public final class JoinedGameMessage2 extends SerMessage{
    private String nickname;
    public JoinedGameMessage2(String nickname) {
        super(nickname);
    }
}
