package org.mio.progettoingsoft.network.messages;

import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.model.enums.MeteorType;

public final class MeteorMessage extends Message {
    private final MeteorType type;
    private final Direction direction;
    private final int number;

    public MeteorMessage(int gameId, String nickname, MeteorType type, Direction direction, int number) {
        super(gameId, nickname);
        this.type = type;
        this.direction = direction;
        this.number = number;
    }

    public MeteorType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getNumber() {
        return number;
    }
}
