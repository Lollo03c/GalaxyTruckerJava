package org.mio.progettoingsoft.exceptions;

public class NotEnoughCrewException extends RuntimeException {
    public NotEnoughCrewException() {
        super("Non è rimasto alcun membro dell'equipaggio");
    }
}
