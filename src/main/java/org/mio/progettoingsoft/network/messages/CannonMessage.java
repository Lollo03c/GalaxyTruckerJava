package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.model.enums.CannonType;

public final class CannonMessage extends Message {
    private final CannonType type;
    private final Direction direction;
    private final int number;


    public CannonMessage(int gameId, String nickname, CannonType type, Direction direction, int number) {
        super(gameId, nickname);
        this.type = type;
        this.direction = direction;
        this.number = number;
    }

    public CannonType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getNumber() {
        return number;
    }
}
