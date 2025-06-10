package org.mio.progettoingsoft.exceptions;

public class NotYourTurnException extends RuntimeException {
    public NotYourTurnException() {
        super("You cannot draw a card : you are not the leader");
    }
}
