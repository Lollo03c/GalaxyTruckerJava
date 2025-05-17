package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.Cordinate;

import java.io.Serializable;

public final class ComponentMessage extends Message {
    public enum Action implements Serializable {
        ADD, REMOVE;
    }

    private final Action action;
    private final int idComp;
    private final Cordinate cordinate;
    private final int rotations;

    public ComponentMessage(int gameId, String nickname, Action action, int idComp, Cordinate cordinate, int rotations) {
        super(gameId, nickname);
        this.action = action;
        this.idComp = idComp;
        this.cordinate = cordinate;
        this.rotations = rotations;
    }

    public Action getAction() {
        return action;
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
