package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

public class Engine extends Component {

    private Direction direction;

    public Engine(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.ENGINE, topConn, bottomConn, rightConn, leftConn);

        this.direction = Direction.BACK;
    }

    protected Engine(int id, ComponentType type,Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.ENGINE, topConn, bottomConn, rightConn, leftConn);

        this.direction = Direction.BACK;
    }

    public void setDirection(Direction dir){
        direction = dir;
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
}
