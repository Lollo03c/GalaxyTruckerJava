package org.mio.progettoingsoft.exceptions;

public class BadPlayerException extends RuntimeException {
    public BadPlayerException(String message) {
        super("The player can't play the card " + message + " at the moment");
    }
}
