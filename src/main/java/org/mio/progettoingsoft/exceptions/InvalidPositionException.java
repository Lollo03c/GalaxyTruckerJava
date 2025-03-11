package org.mio.progettoingsoft.exceptions;

public class InvalidPositionException extends RuntimeException {
    public InvalidPositionException(String message) {
        super(message);
    }

    public InvalidPositionException(int row, int col){
      super("Position ( " + row + " , " + col + " ) is not valid.\n");
    }
}
