package org.mio.progettoingsoft.network.messages;

public final class AdvancePlayerMessage extends Message{
    private final int steps;
    private final int energyToRemove;
    public AdvancePlayerMessage(int gameId, String nickname, int steps, int energyToRemove) {
        super(gameId, nickname);
        this.steps = steps;
        this.energyToRemove = energyToRemove;
    }

    public int getSteps() {
        return steps;
    }
    public int getEnergyToRemove() {return energyToRemove;}
}
