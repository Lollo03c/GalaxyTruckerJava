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
}
