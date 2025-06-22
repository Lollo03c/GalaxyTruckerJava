package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

public class DoubleEngine extends Engine {

    private final int enginePower = 2;

    public DoubleEngine(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.DOUBLE_ENGINE, topConn, bottomConn, rightConn, leftConn);
    }

    public int getEnginePower(boolean activated){
        if (activated)
            return enginePower;

        return 0;
    }

    public String toString(){
        return super.toString() + " Direction: " + getDirection();
    }
}
