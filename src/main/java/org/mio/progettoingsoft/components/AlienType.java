package org.mio.progettoingsoft.components;

public enum AlienType {
    PURPLE, BROWN, NOALIEAN;

    public static AlienType stringToAlienType(String t){
        return switch (t){
            case "purple" -> AlienType.PURPLE;
            case "brown" -> AlienType.BROWN;
            default ->  AlienType.NOALIEAN;
        };
    }
    public String alienToColor(){
        return switch (this){
            case PURPLE -> "\u001B[35m";
            case BROWN -> "\u001B[33m";
            case NOALIEAN -> "\u001B[31m";
        };
    }

    @Override
    public String toString(){
        return switch (this){
            case PURPLE -> "PURPLE";
            case BROWN -> "BROWS";
            case NOALIEAN -> "NO_ALIEN";
        };
    }
}
