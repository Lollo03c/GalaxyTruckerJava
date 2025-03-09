package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.components.GoodType;

public class NotEnoughGoods extends RuntimeException {
    public NotEnoughGoods(String message) {
        super(message);
    }

    public  NotEnoughGoods(GoodType type){
      super("Not enough goods : " + type);
    }
}
