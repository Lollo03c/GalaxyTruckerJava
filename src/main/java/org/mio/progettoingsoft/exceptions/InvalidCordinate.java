package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.model.Cordinate;

/**
 * throw when tried to create a new {@link Cordinate} with invalid attributes
 */
public class InvalidCordinate extends RuntimeException {
    public InvalidCordinate(String message) {
        super(message);
    }
}
