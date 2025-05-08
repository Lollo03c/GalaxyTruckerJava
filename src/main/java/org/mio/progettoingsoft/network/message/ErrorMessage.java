package org.mio.progettoingsoft.network.message;

public final class ErrorMessage extends Message {
    private final ErrorType errorType;

    public ErrorMessage(int idGame, String nickname, ErrorType errorType){
        super(idGame, nickname);
        this.errorType =errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
