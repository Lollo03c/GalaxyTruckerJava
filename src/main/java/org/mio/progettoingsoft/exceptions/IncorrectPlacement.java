package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.Component;

public class IncorrectPlacement extends RuntimeException {
    public IncorrectPlacement(String message) {
        super(message);
    }
    public IncorrectPlacement(int row, int column, Component component){
        super("Cannot place + " + component + " in position (" + row + ", " + column + ")\n" );
    }
}
