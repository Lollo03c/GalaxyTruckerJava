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
}
