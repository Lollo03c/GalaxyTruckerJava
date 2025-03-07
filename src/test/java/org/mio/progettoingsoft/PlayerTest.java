package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.EnergyDepot;
import org.mio.progettoingsoft.components.Housing;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Connector top = Connector.FLAT;
    Connector bottom = Connector.FLAT;
    Connector left = Connector.FLAT;
    Connector right = Connector.FLAT;

    @Test
    public void shuold_add_batteries(){
        Component c1 = new EnergyDepot(1, false, top, bottom, right, left);
        Component c2 = new EnergyDepot(1, false, top, bottom, right, left);
        Component c3 = new Housing(1, top, bottom, right, left);

        Player player = new Player("test");
        player.addCompoment(c1, 1, 1, 0);
        player.addCompoment(c2, 2, 2, 0);
        player.addCompoment(c3, 3, 3, 0);

        assertEquals(4, player.getShipBoard().getQuantBatteries());

    }

}