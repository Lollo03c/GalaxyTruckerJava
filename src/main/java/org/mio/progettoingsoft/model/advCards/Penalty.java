package org.mio.progettoingsoft.model.advCards;

import org.mio.progettoingsoft.model.enums.Direction;
import org.mio.progettoingsoft.model.FlyBoard;
import org.mio.progettoingsoft.model.Player;
import org.mio.progettoingsoft.model.enums.CannonType;

import java.util.List;

public abstract class Penalty {
    private PenaltyType type;
    public abstract PenaltyType getType();
    public CannonType getCannonType() {
        return null;
    }
    public void apply(String json, Player player) throws Exception{}

    public void apply(FlyBoard board, Player player){}

    public void apply(FlyBoard board, Player player, List<Integer[]> housingToRemoveCrew){}

    public int getAmount(){
        throw new RuntimeException("This penalty has no amount");
    }

    public Direction getDirection() {
        throw new RuntimeException("This penalty has no direction");
    }
}
