package org.mio.progettoingsoft.network.message;

public final class CoveredComponentMessage extends Message {

    private final int idComp;

    public CoveredComponentMessage(int idGame, String nickname, int idComp){
        super(idGame, nickname);
        this.idComp = idComp;
    }

    public int getIdComp() {
        return idComp;
    }
}
