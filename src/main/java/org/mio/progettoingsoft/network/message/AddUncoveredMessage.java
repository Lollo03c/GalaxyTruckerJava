package org.mio.progettoingsoft.network.message;

public final class AddUncoveredMessage extends Message{
    private final int idComp;

    public AddUncoveredMessage(Integer idGame, String nickname, int idComp) {
        super(idGame, nickname);
        this.idComp = idComp;
    }

    public int getIdComp() {
        return idComp;
    }
}
