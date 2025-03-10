package org.mio.progettoingsoft.exceptions;

public class InvalidPosition extends RuntimeException {
    public InvalidPosition(String message) {
        super(message);
    }

    public InvalidPosition(int row, int col){
      super("Position ( " + row + " , " + col + " ) is not valid.\n");
    }
}
