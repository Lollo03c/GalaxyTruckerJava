package org.mio.progettoingsoft.components;

public enum GoodType {
    BLUE(1), GREEN(2), YELLOW(3), RED(4);

    GoodType(int value){
        this.value = value;
    }

    private final int value;

    public int getValue(){
        return value;
    }

    @Override
    public String toString(){
        return switch (this){
            case RED -> "RED";
            case BLUE -> "BLUE";
            case GREEN -> "GREEN";
            case YELLOW -> "YELLOW";
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
