package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.*;

public class OpenSpace extends AdventureCard {
    public OpenSpace(int id, int level) {
        super(id, level, AdvCardType.OPEN_SPACE);
    }

    @Override
    public void start(FlyBoard board){
        this.controller.ControllerCards(this, board);
    }
}
