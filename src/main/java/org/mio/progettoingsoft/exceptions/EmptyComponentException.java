package org.mio.progettoingsoft.exceptions;

public class EmptyComponentException extends RuntimeException {
    public EmptyComponentException(String message) {
        super(message);
    }

    public EmptyComponentException(int r, int c){
        super("No component in position ( " + r + ", " + c + " )\n");
    }
}
