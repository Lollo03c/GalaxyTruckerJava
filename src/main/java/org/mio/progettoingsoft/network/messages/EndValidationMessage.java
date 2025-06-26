package org.mio.progettoingsoft.network.messages;

public final class EndValidationMessage extends Message{

    private final boolean usedBattery;

    public EndValidationMessage(int gameId, String nickname, boolean usedBattery) {
        super(gameId, nickname);
        this.usedBattery = usedBattery;
    }

    public boolean isUsedBattery() {
        return usedBattery;
    }
}
