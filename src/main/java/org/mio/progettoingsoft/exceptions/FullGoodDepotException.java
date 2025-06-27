package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.model.components.GoodType;

public class FullGoodDepotException extends RuntimeException {
    public FullGoodDepotException(String message) {
        super(message);
    }
    public FullGoodDepotException(GoodType type){
      super("Not enough space to store : " + type);
    }
}
