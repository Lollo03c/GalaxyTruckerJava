package org.mio.progettoingsoft.network.message;

import org.mio.progettoingsoft.Cordinate;

public final class AddComponentMessage extends Message {
    private final int idComp;
    private final Cordinate cordinate;
    private final int rotations;

    public AddComponentMessage(Integer idGame, String nickname, int idComp, Cordinate cordinate, int rotations) {
        super(idGame, nickname);
        this.idComp = idComp;
        this.cordinate = cordinate;
        this.rotations = rotations;
    }

    public int getIdComp() {
        return idComp;
    }

    public Cordinate getCordinate() {
        return cordinate;
    }

    public int getRotations() {
        return rotations;
    }
}
