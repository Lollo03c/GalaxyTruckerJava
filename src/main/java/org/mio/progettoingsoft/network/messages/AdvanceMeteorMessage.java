package org.mio.progettoingsoft.network.messages;

public final class AdvanceMeteorMessage extends Message{
    private final boolean destroyed;
    private final boolean energy;

    public AdvanceMeteorMessage(int gameId, String nickname, boolean destroyed, boolean energy) {
        super(gameId, nickname);
        this.destroyed = destroyed;
        this.energy = energy;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isEnergy() {
        return energy;
    }
}
