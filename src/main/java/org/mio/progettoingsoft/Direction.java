package org.mio.progettoingsoft;

import java.io.Serializable;

/**
 * Represents the four cardinal directions: FRONT, BACK, RIGHT, LEFT.
 * This enum is used to define orientation, movement, or facing in the game,
 * particularly for components on a ship board. It provides utility methods
 * for finding opposite directions, converting to/from strings, and calculating
 * coordinate offsets based on direction.
 */
public enum Direction implements Serializable {
    FRONT, BACK, RIGHT, LEFT;

    /**
     * Returns the opposite direction to the current direction.
     * For example, the opposite of FRONT is BACK, and the opposite of LEFT is RIGHT.
     *
     * @return The {@link Direction} opposite to this one.
     */
    public Direction getOpposite(){
        return switch (this){
            case LEFT -> RIGHT;
            case BACK -> FRONT;
            case RIGHT -> LEFT;
            case FRONT -> BACK;
        };
    }

    /**
     * Returns the lowercase string representation of this direction (e.g., "front", "back").
     *
     * @return The lowercase string name of the direction.
     */
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

    /**
     * Converts an uppercase string representation of a direction to its corresponding
     * {@code Direction} enum constant.
     * If the string does not match any known direction, it defaults to {@code Direction.FRONT}.
     *
     * @param d The uppercase string to convert (e.g., "FRONT", "BACK").
     * @return The {@code Direction} enum constant.
     */
    public static Direction stringToDirection(String d){
        return switch (d) {
            case "FRONT" -> Direction.FRONT;
            case "BACK" -> Direction.BACK;
            case "RIGHT" -> Direction.RIGHT;
            case "LEFT" -> Direction.LEFT;
            default -> Direction.FRONT;
        };
    }

    /**
     * Returns the column offset associated with this direction.
     * Moving RIGHT results in a +1 column offset.
     * Moving LEFT results in a -1 column offset.
     * Moving FRONT or BACK results in a 0 column offset.
     *
     * @return An integer representing the change in column index.
     */
    public int offsetCol(){
        return switch (this){
            case RIGHT -> 1;
            case LEFT -> -1;
            default -> 0;
        };
    }

    /**
     * Returns the row offset associated with this direction.
     * Moving FRONT results in a -1 row offset (moving up on a typical grid).
     * Moving BACK results in a +1 row offset (moving down on a typical grid).
     * Moving RIGHT or LEFT results in a 0 row offset.
     *
     * @return An integer representing the change in row index.
     */
    public int offsetRow(){
        return switch (this){
            case FRONT -> -1;
            case BACK -> 1;
            default -> 0;
        };
    }
}
