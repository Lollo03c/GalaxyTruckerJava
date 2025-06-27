package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.model.Component;

public class IncorrectPlacementException extends RuntimeException {
    public IncorrectPlacementException(String message) {
        super(message);
    }
    public IncorrectPlacementException(int row, int column, Component component){
        super("Cannot place " + component + " in position (" + row + ", " + column + ")\n" );
    }
}
