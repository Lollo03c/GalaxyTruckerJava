package org.mio.progettoingsoft;

public enum Direction {
    FRONT, BACK, RIGHT, LEFT;

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
