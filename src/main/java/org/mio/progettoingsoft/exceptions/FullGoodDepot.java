package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.components.GoodType;

public class FullGoodDepot extends RuntimeException {
    public FullGoodDepot(String message) {
        super(message);
    }
    public FullGoodDepot(GoodType type){
      super("Not enough space to store : " + type);
    }
}
