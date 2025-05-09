package org.mio.progettoingsoft.exceptions;

/**
 * throw when tried to create a new {@link org.mio.progettoingsoft.Cordinate} with invalid attributes
 */
public class InvalidCordinate extends RuntimeException {
    public InvalidCordinate(String message) {
        super(message);
    }
}
