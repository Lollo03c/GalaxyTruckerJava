package org.mio.progettoingsoft.network.input;

public final class IntInput extends Input{
    private final int number;

    public IntInput(int number){
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
