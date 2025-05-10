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

    @Override
    public String toString(){
        return switch (this){
            case PURPLE -> "PURPLE";
            case BROWN -> "BROWS";
            case HUMAN -> "HUMAN";
        };
    }
}
