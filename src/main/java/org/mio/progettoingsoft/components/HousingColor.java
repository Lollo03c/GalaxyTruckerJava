package org.mio.progettoingsoft.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum HousingColor implements Serializable {
    YELLOW, BLUE, RED, GREEN;

    public static HousingColor stringToColor(String s){
        return switch(s){
            case "blue" -> HousingColor.BLUE;
            case "red" -> HousingColor.RED;
            case "yellow" -> HousingColor.YELLOW;
            case "green" -> HousingColor.GREEN;
            default -> HousingColor.BLUE;
        };
    }


    public static HousingColor getHousingColorById(int id){
        return switch (id) {
            case 33 -> HousingColor.BLUE;
            case 34 -> HousingColor.GREEN;
            case 52 -> HousingColor.RED;
            case 61 -> HousingColor.YELLOW;
            default -> HousingColor.BLUE;
        };
    }

    public int getIdByColor(){
        return switch (this) {
            case BLUE -> 33;
            case GREEN -> 34;
            case RED -> 52;
            case YELLOW -> 61;
        };
    }

    public String colorToString(){
        return switch (this){
            case RED -> "\u001B[31m";
            case BLUE -> "\u001B[34m";
            case GREEN -> "\u001B[32m";
            case YELLOW -> "\u001B[33m";
        };
    }

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
