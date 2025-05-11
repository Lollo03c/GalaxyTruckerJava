package org.mio.progettoingsoft;

import org.mio.progettoingsoft.exceptions.InvalidCordinate;

import java.util.ArrayList;
import java.util.List;

public class Cordinate {
    private final int row;
    private final int column;

    private final static int maxRow = 4;
    private final static int maxCol = 6;

    public Cordinate(int row, int column) throws InvalidCordinate {
        if (row < 0 || row > maxRow || column < 0 || column > maxCol)
            throw new InvalidCordinate("");

        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean Equals(int r, int c){
        return r == row && c == column;
    }

    public List<Cordinate> getAdjacent(){
        List<Cordinate> adj = new ArrayList<>();

        try{
            adj.add(new Cordinate(row-1, column));
        } catch (InvalidCordinate e){

        }

        try{
            adj.add(new Cordinate(row+1, column));
        } catch (InvalidCordinate e){

        }

        try{
            adj.add(new Cordinate(row, column-1));
        } catch (InvalidCordinate e){

        }

        try{
            adj.add(new Cordinate(row, column+1));
        } catch (InvalidCordinate e){

        }

        return adj;
    }
}
