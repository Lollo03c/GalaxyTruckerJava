package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

public class Drill extends Component {

    private Direction direction;

    public Drill(Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(ComponentType.DRILL, topConn, bottomConn, rightConn, leftConn);
    }

    protected Drill(ComponentType type, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(type, topConn, bottomConn, rightConn, leftConn);
    }

    public Direction getDirection(){
        return this.direction;
    }

    public void setDirection(Direction dir){
        this.direction = dir;
    }
}
