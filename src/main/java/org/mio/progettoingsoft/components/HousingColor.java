package org.mio.progettoingsoft.components;

public enum HousingColor {
    BLUE(1), GREEN(2), RED(3), YELLOW(4);

    HousingColor(int value){
        this.value = value;
    }

    private final int value;

    public int getValue(){
        return value;
    }
    public static HousingColor  stringToColor(String s){
        return switch(s){
            case "blue" -> HousingColor.BLUE;
            case "red" -> HousingColor.RED;
            case "yellow" -> HousingColor.YELLOW;
            case "green" -> HousingColor.GREEN;
            default -> HousingColor.BLUE;
        };
    }
}
