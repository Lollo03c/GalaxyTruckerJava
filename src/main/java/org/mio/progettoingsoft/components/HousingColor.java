package org.mio.progettoingsoft.components;

public enum HousingColor {
    BLUE, GREEN, RED, YELLOW, NOCOLOR;

    public static HousingColor stringToColor(String s){
        return switch(s){
            case "blue" -> HousingColor.BLUE;
            case "red" -> HousingColor.RED;
            case "yellow" -> HousingColor.YELLOW;
            case "green" -> HousingColor.GREEN;
            default -> HousingColor.BLUE;
        };
    }
    public String colorToString(){
        return switch (this){
            case RED -> "\u001B[31m";
            case BLUE -> "\u001B[34m";
            case GREEN -> "\u001B[32m";
            case YELLOW -> "\u001B[33m";
            case NOCOLOR -> "\u001B[0m";
        };
    }

    public static int getIdByColor(HousingColor color){
        return switch (color) {
            case BLUE -> 33;
            case GREEN -> 34;
            case RED -> 52;
            case YELLOW -> 61;
            //aggiunto NOCOLOR HO MESSO UN ID A CASO
            case NOCOLOR -> 100;
        };
    }
}
