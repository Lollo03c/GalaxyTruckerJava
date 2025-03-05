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
}
