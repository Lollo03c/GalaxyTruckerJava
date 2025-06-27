package org.mio.progettoingsoft.model.components;

import java.util.List;

/**
 * Represents the different types (colors) of goods in the game.
 * Each good type has an associated numerical value and a string representation.
 * It also provides utility methods for converting strings to GoodType and for
 * obtaining ANSI escape codes for colored console output.
 */
public enum GoodType {
    BLUE(1), GREEN(2), YELLOW(3), RED(4);

    private final int value;
    public static List<GoodType> sortedList = List.of(RED, YELLOW, GREEN, BLUE);

    /**
     * Constructs a GoodType enum constant with the specified value.
     * @param value The numerical value to associate with this good type.
     */
    GoodType(int value){
        this.value = value;
    }

    /**
     * Returns the numerical value of this good type.
     * @return The integer value.
     */
    public int getValue(){
        return value;
    }

    /**
     * Returns the string representation of this good type (e.g., "RED", "BLUE").
     * @return The uppercase string name of the good type.
     */
    @Override
    public String toString(){
        return switch (this){
            case RED -> "RED";
            case BLUE -> "BLUE";
            case GREEN -> "GREEN";
            case YELLOW -> "YELLOW";
        };
    }

    /**
     * Returns the ANSI escape code for the color associated with this good type.
     * This can be used for console output to display text in the corresponding color.
     *
     * @return The ANSI escape code string.
     */
    public String toColor(){
        return switch (this){
            case RED -> "\u001B[31m";
            case BLUE -> "\u001B[34m";
            case GREEN -> "\u001B[32m";
            case YELLOW -> "\u001B[33m";
        };
    }

    /**
     * Converts a string to its corresponding {@code GoodType} enum constant.
     * The comparison is case-sensitive and expects uppercase names (e.g., "RED").
     * If the string does not match any known good type, it defaults to {@code GoodType.BLUE}.
     *
     * @param s The string to convert.
     * @return The {@code GoodType} enum constant, or {@code GoodType.BLUE} if no match.
     */
    public static GoodType stringToGoodType(String s){
        return switch (s){
            case "BLUE" -> GoodType.BLUE;
            case "GREEN"-> GoodType.GREEN;
            case "YELLOW"-> GoodType.YELLOW;
            case "RED" -> GoodType.RED;
            default -> GoodType.BLUE;
        };
    }
}
