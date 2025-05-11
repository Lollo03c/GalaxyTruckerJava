package org.mio.progettoingsoft.components;

import java.util.List;

public enum GoodType {
    BLUE(1), GREEN(2), YELLOW(3), RED(4);

    GoodType(int value){
        this.value = value;
    }

    private final int value;

    public int getValue(){
        return value;
    }

    public static List<GoodType> sortedList = List.of(RED, YELLOW, GREEN, BLUE);

    @Override
    public String toString(){
        return switch (this){
            case RED -> "RED";
            case BLUE -> "BLUE";
            case GREEN -> "GREEN";
            case YELLOW -> "YELLOW";
        };

    }
    public String toColor(){
        return switch (this){
            case RED -> "\u001B[31m";
            case BLUE -> "\u001B[34m";
            case GREEN -> "\u001B[32m";
            case YELLOW -> "\u001B[33m";
        };
    }

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
