package org.mio.progettoingsoft.advCards;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.ShipBoard;

import java.util.Optional;

public abstract class CannonPenalty extends Penalty {
    protected final Direction direction;

    public CannonPenalty(Direction direction) {
        this.direction = direction;
    }

    public static CannonPenalty stringToCannonPenalty(String type, String direction) {
        if(type.equals("LIGHT")) {
            return new LightCannon(Direction.stringToDirection(direction));
        }else{
            return new HeavyCannon(Direction.stringToDirection(direction));
        }
    }

    protected Optional<Component> findHit(Player player, int value){
        ShipBoard board = player.getShipBoard();
        Optional<Component>[][] shipComponents = board.getComponentsMatrix();

        switch (direction){
            case LEFT -> value -= board.getOffsetCol();
            case RIGHT -> value -= board.getOffsetCol();
            case FRONT -> value -= board.getOffsetRow();
            case BACK -> value -= board.getOffsetRow();
        }

        Optional<Component> hitComponent = Optional.empty();
        if (direction.equals(Direction.FRONT)){
            int row = 0;
            while (row < board.getRows() && shipComponents[row][value].isEmpty())
                row++;
            if (row < board.getRows())
                hitComponent = shipComponents[row][value];
        }
        if (direction.equals(Direction.BACK)){
            int row = board.getRows();
            while (row >= 0 && shipComponents[row][value].isEmpty())
                row--;
            if (row >= 0)
                hitComponent = shipComponents[row][value];
        }
        if (direction.equals(Direction.LEFT)){
            int col = 0;
            while (col < board.getRows() && shipComponents[value][col].isEmpty())
                col++;
            if (col < board.getColumns())
                hitComponent = shipComponents[value][col];
        }
        if (direction.equals(Direction.RIGHT)){
            int col = board.getColumns();
            while (col >= 0 && shipComponents[value][col].isEmpty())
                col--;
            if (col >= 0)
                hitComponent = shipComponents[value][col];
        }
        return hitComponent;
    }
}
