package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Shield extends Component {
    private Set<Direction> directions;

    public Shield(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.SHIELD, topConn, bottomConn, rightConn, leftConn);

        directions = new HashSet<>(2);
        directions.add(Direction.FRONT);
        directions.add(Direction.RIGHT);
    }

    public Boolean isCovered(Direction dir){
        return directions.contains(dir);
    }
}
