package org.mio.progettoingsoft.network.messages;

public final class LandOnPlanetMessage extends Message{
    private final int choice;
    public LandOnPlanetMessage(int idGame, String nickname, int choice) {
        super(idGame, nickname);
        this.choice = choice;
    }
    public int getChoice() {
        return choice;
    }

}
