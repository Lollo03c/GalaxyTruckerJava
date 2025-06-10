package org.mio.progettoingsoft.network.messages;

public final class BuildShipMessage extends Message{

    private final int indexShip;

    public BuildShipMessage(int gameId, String nickname, int indexShip) {
        super(gameId, nickname);
        this.indexShip = indexShip;
    }

    public int getIndexShip() {
        return indexShip;
    }
}
