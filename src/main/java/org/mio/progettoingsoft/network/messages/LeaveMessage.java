package org.mio.progettoingsoft.network.messages;

public final class LeaveMessage extends Message{
    public LeaveMessage(int idGame, String nickname) {
        super(idGame, nickname);
    }
}
