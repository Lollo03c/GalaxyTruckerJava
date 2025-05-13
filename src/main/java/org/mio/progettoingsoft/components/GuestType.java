package org.mio.progettoingsoft.components;

public enum GuestType {
    PURPLE, BROWN, HUMAN;

    public static GuestType stringToAlienType(String t){
        return switch (t){
            case "purple" -> GuestType.PURPLE;
            case "brown" -> GuestType.BROWN;
            default ->  GuestType.HUMAN;
        };
    }

    public String guestToColor(){
        return switch (this){
            case PURPLE -> "\u001B[35m";
            case BROWN -> "\u001B[33m";
            case HUMAN -> "non importante";
        };
    }
    @Override
    public String toString(){
        return switch (this){
            case PURPLE -> "PURPLE";
            case BROWN -> "BROWS";
            case HUMAN -> "HUMAN";
        };
    }
}
