package org.mio.progettoingsoft.network.message;

public final class AddBookedMessage extends Message{

    private final int addedComp;
    private final int toPosition;

    public AddBookedMessage(Integer idGame, String nickname, int addedComp, int toPosition) {
        super(idGame, nickname);
        this.toPosition = toPosition;
        this.addedComp = addedComp;
    }

    public int getToPosition() {
        return toPosition;
    }

    public int getAddedCompId() {
        return addedComp;
    }
}
