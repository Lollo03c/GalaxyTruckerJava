package org.mio.progettoingsoft.network.server.messages;

public final class SetNicknameMessage extends Message{
    private final boolean accepted;
    private final boolean toSetup;

    public SetNicknameMessage(int idGame, String nickname, boolean accepted, boolean toSetup) {
        super(idGame, nickname);
        this.accepted = accepted;
        this.toSetup = toSetup;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isToSetup() {
        return toSetup;
    }
}
