package org.mio.progettoingsoft.components;

/**
 * Represents the different types (colors/species) of guests that can be housed in a ship's components.
 * This enum differentiates between alien guests (PURPLE, BROWN) and humans.
 * It provides utility methods for converting strings to GuestType and for
 * obtaining ANSI escape codes for colored console output for aliens.
 */
public enum GuestType {
    PURPLE, BROWN, HUMAN;

    /**
     * Converts a string to its corresponding {@code GuestType} enum constant.
     * The comparison is case-insensitive.
     * If the string does not match "purple" or "brown", it defaults to {@code GuestType.HUMAN}.
     *
     * @param t The string to convert (e.g., "purple", "brown", "human").
     * @return The {@code GuestType} enum constant.
     */
    public static GuestType stringToAlienType(String t){
        return switch (t){
            case "purple" -> GuestType.PURPLE;
            case "brown" -> GuestType.BROWN;
            default ->  GuestType.HUMAN;
        };
    }

    /**
     * Returns the ANSI escape code for the color associated with alien guest types.
     * This can be used for console output to display text in the corresponding color.
     * For HUMAN guests, it returns a placeholder string as color might not be applicable
     * or a specific color isn't intended for console output for humans.
     *
     * @return The ANSI escape code string for alien colors, or a placeholder for humans.
     */
    public String guestToColor(){
        return switch (this){
            case PURPLE -> "\u001B[35m";
            case BROWN -> "\u001B[33m";
            case HUMAN -> "non importante";
        };
    }

    /**
     * Returns the uppercase string representation of this guest type (e.g., "PURPLE", "BROWN", "HUMAN").
     * @return The uppercase string name of the guest type.
     */
    @Override
    public String toString(){
        return switch (this){
            case PURPLE -> "PURPLE";
            case BROWN -> "BROWN";
            case HUMAN -> "HUMAN";
        };
    }
}
