package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

public class Pipe extends Component {

    public Pipe(Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn){
        super(ComponentType.PIPE, topConn, bottomConn, rightConn, leftConn);
    }
}
