package org.mio.progettoingsoft;

import org.mio.progettoingsoft.exceptions.InvalidCordinate;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a coordinate (row, column) on a game board.
 * This class ensures that coordinates are within a predefined valid range and provides
 * utility methods for equality, hashing, finding adjacent coordinates, and iterating through all possible coordinates.
 * Coordinates are serializable for persistence.
 */
public class Cordinate implements Serializable {
    private final int row;
    private final int column;

    private final static int maxRow = 4;
    private final static int maxCol = 6;

    /**
     * Constructs a new Cordinate object.
     *
     * @param row The row index.
     * @param column The column index.
     * @throws InvalidCordinate if the provided row or column is outside the valid range [0, maxRow] or [0, maxCol].
     */
    public Cordinate(int row, int column) throws InvalidCordinate {
        if (row < 0 || row > maxRow || column < 0 || column > maxCol)
            throw new InvalidCordinate("");

        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row component of this coordinate.
     *
     * @return The row index.
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column component of this coordinate.
     *
     * @return The column index.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Compares this Cordinate object with another object for equality.
     * Two Cordinate objects are considered equal if they have the same row and column values.
     *
     * @param obj The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Cordinate other = (Cordinate) obj;
        return row == other.row && column == other.column;
    }

    /**
     * Returns a hash code value for this Cordinate object.
     * The hash code is based on the row and column values.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * Returns a list of valid adjacent coordinates (up, down, left, right).
     * Coordinates that fall outside the board boundaries are not included in the list.
     *
     * @return A {@link List} of {@link Cordinate} objects representing valid adjacent positions.
     */
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

    /**
     * Returns a static {@link Iterator} that can iterate through all possible valid {@link Cordinate} objects
     * on the board, starting from (0,0) and proceeding row by row.
     *
     * @return An iterator for {@link Cordinate} objects.
     */
    public static Iterator<Cordinate> getIterator(){
        return new Iterator<Cordinate>() {
            private int currentRow = 0;
            private int currentCol = 0;

            @Override
            public boolean hasNext() {
                return currentRow < 5;
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

    /**
     * Returns a string representation of the Cordinate in a user-friendly format,
     * adding offsets to the row and column for display purposes (e.g., (Row + 5, Col + 4)).
     *
     * @return A formatted string like "(R+5, C+4)".
     */
    @Override
    public String toString(){
        return "(" + (row + 5) + ", " + (column + 4) + ")";
    }

    /**
     * Returns a string representation of the Cordinate displaying its actual internal values.
     *
     * @return A string like "( R, C )".
     */
    public String realValue(){
        return "( " + row + ", " + column + ") ";
    }
}