package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;
import java.util.HashSet;
import java.util.Iterator;

import java.util.*;

public class Shield extends Component {
    private List<Direction> directions;

    public Shield(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.SHIELD, topConn, bottomConn, rightConn, leftConn);

        directions = new ArrayList<>(2);
        directions.add(Direction.FRONT);
        directions.add(Direction.RIGHT);
    }
    @Override
    public void rotateClockwise(){
        super.rotateClockwise();
        directions.set(0, switch (directions.get(0)) {
            case FRONT -> Direction.RIGHT;
            case RIGHT -> Direction.BACK;
            case BACK -> Direction.LEFT;
            case LEFT -> Direction.FRONT;
        });
        directions.set(1, switch (directions.get(1)) {
            case FRONT -> Direction.RIGHT;
            case RIGHT -> Direction.BACK;
            case BACK -> Direction.LEFT;
            case LEFT -> Direction.FRONT;
        });
    }
    public void rotateCounterClockwise(){
        super.rotateClockwise();
        directions.set(0, switch (directions.get(0)) {
            case FRONT -> Direction.LEFT;
            case RIGHT -> Direction.FRONT;
            case BACK -> Direction.RIGHT;
            case LEFT -> Direction.BACK;
        });
        directions.set(1, switch (directions.get(1)) {
            case FRONT -> Direction.LEFT;
            case RIGHT -> Direction.FRONT;
            case BACK -> Direction.RIGHT;
            case LEFT -> Direction.BACK;
        });
    }
    public Boolean isCovered(Direction dir){
        return directions.contains(dir);
    }
}
