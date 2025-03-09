package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.components.AlienType;

public class NotEnoughHousing extends RuntimeException{
    public NotEnoughHousing(AlienType type){
        super("Not enough space for " + type + " alien\n");
    }

    public NotEnoughHousing(){
        super("Not enough space for another human\n");
    }

}
