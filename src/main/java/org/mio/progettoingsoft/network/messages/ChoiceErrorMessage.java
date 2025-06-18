package org.mio.progettoingsoft.network.messages;

public final class ChoiceErrorMessage extends Message {
    public String message;
    public ChoiceErrorMessage(int idGame, String nickname, String message) {
        super(idGame, nickname);
        this.message = message;
    }
    public String getMsg() {
        return message;
    }
}
