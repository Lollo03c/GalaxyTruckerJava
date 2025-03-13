package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;

public abstract class Meteor {
    protected final Direction direction;

    public Meteor(Direction direction) {
        this.direction = direction;
    }

    public static Meteor stringToMeteor(String type, String direction) {
        if(type.equals("SMALL")){
            return new SmallMeteor(Direction.stringToDirection(direction));
        }else{
            return new BigMeteor(Direction.stringToDirection(direction));
        }
    }

    public abstract void hit(Player player, int row, int col);
}
