package org.mio.progettoingsoft.network.messages;

public final class AddPlayerMessage extends Message{
    private final String otherPlayerNickname;
    private final int place;
    public AddPlayerMessage(int idGame, String nickname, String otherPlayerNickname, int place) {
        super(idGame, nickname);
        this.otherPlayerNickname = otherPlayerNickname;
        this.place = place;
    }

    public int getPlace() {
        return place;
    }
    public String getOtherPlayerNickname() {
        return otherPlayerNickname;
    }

}
