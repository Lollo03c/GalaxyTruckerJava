package org.mio.progettoingsoft.model.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the distinct colors associated with initial "Housing" components on the game board.
 * These colors are likely used for specific game mechanics, such as starting positions or alien placement rules.
 * This enum also provides utility methods for converting between string representations,
 * numeric IDs, and the enum constants, as well as obtaining ANSI color codes for display.
 */
public enum HousingColor implements Serializable {
    YELLOW, BLUE, RED, GREEN;

    /**
     * Converts a string representation of a color to its corresponding {@code HousingColor} enum constant.
     * The comparison is case-sensitive (expects lowercase input). If no match is found, it defaults to {@code HousingColor.BLUE}.
     *
     * @param s The string to convert (e.g., "blue", "red", "yellow", "green").
     * @return The {@code HousingColor} enum constant.
     */
    public static HousingColor stringToColor(String s){
        return switch(s){
            case "blue" -> HousingColor.BLUE;
            case "red" -> HousingColor.RED;
            case "yellow" -> HousingColor.YELLOW;
            case "green" -> HousingColor.GREEN;
            default -> HousingColor.BLUE;
        };
    }

    /**
     * Retrieves the {@code HousingColor} enum constant based on a given integer ID.
     * These IDs are specific to certain housing components, likely their unique identifiers on the game board.
     * If the ID does not match any known housing color, it defaults to {@code HousingColor.BLUE}.
     *
     * @param id The integer ID of the housing.
     * @return The {@code HousingColor} enum constant corresponding to the ID.
     */
    public static HousingColor getHousingColorById(int id){
        return switch (id) {
            case 33 -> HousingColor.BLUE;
            case 34 -> HousingColor.GREEN;
            case 52 -> HousingColor.RED;
            case 61 -> HousingColor.YELLOW;
            default -> HousingColor.BLUE;
        };
    }

    /**
     * Returns the integer ID associated with this {@code HousingColor} enum constant.
     * This is the inverse operation of {@code getHousingColorById}.
     *
     * @return The integer ID of the housing color.
     */
    public int getIdByColor(){
        return switch (this) {
            case BLUE -> 33;
            case GREEN -> 34;
            case RED -> 52;
            case YELLOW -> 61;
        };
    }

    /**
     * Returns the ANSI escape code for the color associated with this housing color.
     * This can be used for console output to display text in the corresponding color.
     *
     * @return The ANSI escape code string.
     */
    public String colorToString(){
        return switch (this){
            case RED -> "\u001B[31m";
            case BLUE -> "\u001B[34m";
            case GREEN -> "\u001B[32m";
            case YELLOW -> "\u001B[33m";
        };
    }

    /**
     * Returns a new {@link ArrayList} containing the {@code HousingColor} enum constants
     * in a specific sorted order: BLUE, YELLOW, RED, GREEN.
     * This order might be relevant for iterating through colors in a predefined sequence in game logic.
     *
     * @return A sorted {@link List} of {@code HousingColor} objects.
     */
    public static List<HousingColor> getSorted(){
        return new ArrayList<>(
                List.of(
                        BLUE,
                        YELLOW,
                        RED,
                        GREEN
                )
        );
    }
}
