package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

public class DoubleDrill extends Drill{
    public DoubleDrill(int id, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(id, ComponentType.DOUBLE_DRILL, topConn, bottomConn, rightConn, leftConn);
    }

    @Override
    public Float getFirePower(){
        if(getDirection() == Direction.FRONT)
            return 2f;
        return 1f;
    }
}
