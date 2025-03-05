package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

public class Engine extends Component {

    private Direction direction;

    public Engine(Direction direction, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(ComponentType.ENGINE, topConn, bottomConn, rightConn, leftConn);

        this.direction = direction;
    }

    protected Engine(ComponentType type, Direction direction, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(ComponentType.ENGINE, topConn, bottomConn, rightConn, leftConn);

        this.direction = direction;
    }

    public void setDirection(Direction dir){
        direction = dir;
    }


}
