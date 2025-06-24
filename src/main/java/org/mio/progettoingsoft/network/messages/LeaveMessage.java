package org.mio.progettoingsoft.network.messages;

public final class LeaveMessage extends Message{
    private final boolean leave;

    public LeaveMessage(int idGame, String nickname, boolean leave) {
        super(idGame, nickname);
        this.leave = leave;
    }

    public boolean isLeave() {
        return leave;
    }
}
