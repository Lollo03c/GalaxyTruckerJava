package org.mio.progettoingsoft.network.message;

public final class WelcomeMessage extends Message{

    private final int idPlayer;
    public WelcomeMessage(int idPlayer){
        super(null, null);
        this.idPlayer = idPlayer;
    }

    public int getIdPlayer() {
        return idPlayer;
    }
}
