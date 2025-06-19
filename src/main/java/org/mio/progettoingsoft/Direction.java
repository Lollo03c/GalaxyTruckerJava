package org.mio.progettoingsoft;

import java.io.Serializable;

public enum Direction implements Serializable {
    FRONT, BACK, RIGHT, LEFT;

    public Direction getOpposite(){
        return switch (this){
            case LEFT -> RIGHT;
            case BACK -> FRONT;
            case RIGHT -> LEFT;
            case FRONT -> BACK;
        };
    }
    @Override
    public String toString() {
        switch (this){
            case FRONT : return "front";
            case BACK : return "back";
            case RIGHT : return "right";
            case LEFT : return "left";
            default: return "";
        }
    }

    public static Direction stringToDirection(String d){
        return switch (d) {
            case "FRONT" -> Direction.FRONT;
            case "BACK" -> Direction.BACK;
            case "RIGHT" -> Direction.RIGHT;
            case "LEFT" -> Direction.LEFT;
            default -> Direction.FRONT;
        };
    }

    public int offsetCol(){
        return switch (this){
            case RIGHT -> 1;
            case LEFT -> -1;
            default -> 0;
        };
    }

    public int offsetRow(){
        return switch (this){
            case FRONT -> 1;
            case BACK -> -1;
            default -> 0;
        };
    }
}
