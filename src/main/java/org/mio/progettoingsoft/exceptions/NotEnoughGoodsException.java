package org.mio.progettoingsoft.exceptions;

import org.mio.progettoingsoft.model.components.GoodType;

public class NotEnoughGoodsException extends RuntimeException {
    public NotEnoughGoodsException(String message) {
        super(message);
    }

    public NotEnoughGoodsException(GoodType type){
      super("Not enough goods : " + type);
    }
}
