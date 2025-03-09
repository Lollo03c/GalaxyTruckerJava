package org.mio.progettoingsoft;

public enum Direction {
    FRONT, BACK, RIGHT, LEFT;

    public Direction getOpposite(){
        return switch (this){
            case LEFT -> RIGHT;
            case BACK -> FRONT;
            case RIGHT -> LEFT;
            case FRONT -> BACK;
        };
    }
}
