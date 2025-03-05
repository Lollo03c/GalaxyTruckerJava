package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

public class DoubleDrill extends Drill{
    public DoubleDrill(Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(ComponentType.DOUBLE_DRILL, topConn, bottomConn, rightConn, leftConn);
    }
}
