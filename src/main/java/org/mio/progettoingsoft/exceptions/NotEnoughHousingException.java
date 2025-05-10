package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.components.GuestType;

public class NotEnoughHousingException extends RuntimeException{
    public NotEnoughHousingException(GuestType type){
        super("Not enough space for " + type + " alien\n");
    }

    public NotEnoughHousingException(){
        super("Not enough space for another human\n");
    }

}
