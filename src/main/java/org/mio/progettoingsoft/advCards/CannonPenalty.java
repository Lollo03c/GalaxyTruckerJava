package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Direction;

public abstract class CannonPenalty extends Penalty {
    private final Direction direction;

    public CannonPenalty(Direction direction) {
        this.direction = direction;
    }

    public static CannonPenalty stringToCannonPenalty(String type, String direction) {
        if(type.equals("LIGHT")) {
            return new LightCannon(Direction.stringToDirection(direction));
        }else{
            return new HeavyCannon(Direction.stringToDirection(direction));
        }
    }
}
