package org.mio.progettoingsoft.exceptions;

public class EmptyComponent extends RuntimeException {
    public EmptyComponent(String message) {
        super(message);
    }

    public EmptyComponent(int r, int c){
        super("No component in position ( " + r + ", " + c + " )\n");
    }
}
