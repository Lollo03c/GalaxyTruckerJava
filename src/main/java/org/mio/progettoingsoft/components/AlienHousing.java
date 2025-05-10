package org.mio.progettoingsoft.components;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ComponentType;
import org.mio.progettoingsoft.Connector;

public class AlienHousing extends Component {
    private final GuestType color;

    public AlienHousing(int id, GuestType color, Connector topConn, Connector bottomConn, Connector rightConn, Connector leftConn) {
        super(id, ComponentType.ALIEN_HOUSING,topConn, bottomConn, rightConn, leftConn);

        this.color = color;
    }

    @Override
    public GuestType getColorAlien() {
        return color;
    }
}
