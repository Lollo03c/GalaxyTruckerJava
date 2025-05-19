package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.ShipBoard;

import java.util.Optional;

public abstract class Meteor {
    protected final Direction direction;

    public Meteor(Direction direction) {
        this.direction = direction;
    }

    public static Meteor stringToMeteor(String type, String direction) {
        if(type.equals("SMALL")){
            return new SmallMeteor(Direction.stringToDirection(direction));
        }else{
            return new BigMeteor(Direction.stringToDirection(direction));
        }
    }

    public Direction getDirection(){
        return direction;
    }

    public abstract void hit(Player player, int value);

    protected Optional<Component> findHit(Player player, int value){
        ShipBoard board = player.getShipBoard();
        int rows = board.getRows();
        int columns = board.getColumns();
        Optional<Component>[][] shipComponents = board.getComponentsMatrix();

        int maxValue = direction.equals(Direction.FRONT) || direction.equals(Direction.BACK) ? board.getColumns() : board.getRows();
        Optional<Component> isHit = Optional.empty();

        if (value < 0 || value >= maxValue)
            Optional.empty();
//a cosa serve l'if qui sopra ?
        if (direction.equals(Direction.FRONT)){
            int row = 0;
            while (row < rows && shipComponents[row][value].isEmpty())
                row++;

            if (row < rows){
                isHit = Optional.of(shipComponents[row][value].get());
            }
        }
        else if (direction.equals(Direction.BACK)){
            int row = rows;
            while (row >= 0 && shipComponents[row][value].isEmpty())
                row--;

            if (row >= rows){
                isHit = Optional.of(shipComponents[row][value].get());
            }
        }
        else if (direction.equals(Direction.RIGHT)){
            int col = columns;
            while (col >= 0 && shipComponents[value][col].isEmpty())
                col--;

            if (col >= columns){
                isHit = Optional.of(shipComponents[value][col].get());
            }
        }
        else if (direction.equals(Direction.LEFT)){
            int col = 0;
            while (col < columns && shipComponents[value][col].isEmpty())
                col--;

            if (col < columns){
                isHit = Optional.of(shipComponents[value][col].get());
            }
        }

        return isHit;
    }
}
