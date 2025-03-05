package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

public class AlienHousing extends Component {
    private final AlienType color;

    public AlienHousing(AlienType color, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn) {
        super(ComponentType.ALIEN_HOUSING,topConn, bottomConn, rightConn, leftConn);

        this.color = color;
    }

    public AlienType getColor() {
        return color;
    }
}
