package org.mio.progettoingsoft;

public class Cordinate {
    private final int row;
    private final int column;

    public Cordinate(int row, int column){
        this.row = row;
        this.column = column;
    }

    public boolean Equals(int r, int c){
        return r == row && c == column;
    }
}
