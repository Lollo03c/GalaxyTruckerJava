package org.mio.progettoingsoft.advCards;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;

import java.util.List;

public abstract class Penalty {
    private PenaltyType type;
    public abstract PenaltyType getType();

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
