package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

public class Drill extends Component {

    private Direction direction;

    public Drill(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.DRILL, topConn, bottomConn, rightConn, leftConn);
        direction = Direction.FRONT;
    }

    protected Drill(int id, ComponentType type, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, type, topConn, bottomConn, rightConn, leftConn);
        direction = Direction.FRONT;
    }

    @Override
    public Direction getDirection(){
        return this.direction;
    }

    public void setDirection(Direction dir){
        this.direction = dir;
    }

    @Override
    public void rotateClockwise(){
        super.rotateClockwise();
        this.direction = switch(direction){
            case FRONT -> Direction.RIGHT;
            case RIGHT -> Direction.BACK;
            case BACK -> Direction.LEFT;
            case LEFT -> Direction.FRONT;
        };
    }

    @Override
    public void rotateCounterClockwise(){
        super.rotateCounterClockwise();
        this.direction = switch(direction){
            case FRONT -> Direction.LEFT;
            case LEFT -> Direction.BACK;
            case BACK -> Direction.RIGHT;
            case RIGHT -> Direction.FRONT;
        };
    }

    @Override
    public Float getFirePower(){
        if (direction.equals(Direction.FRONT))
            return 1f;

        return 0.5f;
    }

    public String toString(){
        return super.toString() + " Direction: " + getDirection();
    }
}
