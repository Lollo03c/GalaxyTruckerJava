package org.mio.progettoingsoft;

import org.mio.progettoingsoft.exceptions.InvalidCordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Cordinate implements Serializable {
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

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Cordinate other = (Cordinate) obj;
        return row == other.row && column == other.column;
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

    public static Iterator<Cordinate> getIterator(){
        return new Iterator<Cordinate>() {
            private int currentRow = 0;
            private int currentCol = 0;

            @Override
            public boolean hasNext() {
                return !(currentRow == 4 && currentCol == 6);
            }

            @Override
            public Cordinate next() {
                if (!hasNext())
                    throw new NoSuchElementException();

                Cordinate current = new Cordinate(currentRow, currentCol);
                currentCol++;
                if (currentCol > maxCol){
                    currentCol = 0;
                    currentRow++;
                }

                return current;
            }
        };
    }


}
