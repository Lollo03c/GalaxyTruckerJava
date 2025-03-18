package org.mio.progettoingsoft.components;

public enum HousingColor {
    BLUE, GREEN, RED, YELLOW;

    public static HousingColor stringToColor(String s){
        return switch(s){
            case "blue" -> HousingColor.BLUE;
            case "red" -> HousingColor.RED;
            case "yellow" -> HousingColor.YELLOW;
            case "green" -> HousingColor.GREEN;
            default -> HousingColor.BLUE;
        };
    }

    public static int getIdByColor(HousingColor color){
        return switch (color) {
            case BLUE -> 33;
            case GREEN -> 34;
            case RED -> 52;
            case YELLOW -> 61;
        };
    }
}
