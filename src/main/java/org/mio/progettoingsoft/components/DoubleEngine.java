package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

public class DoubleEngine extends Engine {

    public DoubleEngine(Direction direction, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(ComponentType.DOUBLE_ENGINE, direction, topConn, bottomConn, rightConn, leftConn);
    }
}
