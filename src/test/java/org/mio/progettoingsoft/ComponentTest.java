package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.exceptions.IncorrectShipBoardException;
import org.mio.progettoingsoft.model.Component;
import org.mio.progettoingsoft.model.components.Drill;
import org.mio.progettoingsoft.model.components.GoodType;
import org.mio.progettoingsoft.model.components.GuestType;
import org.mio.progettoingsoft.model.components.Housing;
import org.mio.progettoingsoft.model.enums.Connector;

import static org.junit.jupiter.api.Assertions.*;

class ComponentTest {

    @Test
    void should_throw_exception_if_not_battery_depot(){
        Component comp = new Drill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertThrows(IncorrectShipBoardException.class, () -> comp.removeOneEnergy());
        assertThrows(IncorrectShipBoardException.class, () -> comp.getTriple());
    }

    @Test
    void should_throw_exception_if_not_depot(){
        Component comp = new Drill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertThrows(IncorrectShipBoardException.class, () -> comp.addGood(GoodType.BLUE));
        assertThrows(IncorrectShipBoardException.class, () -> comp.removeGood(GoodType.BLUE));
        assertFalse(comp.canContainsGood(GoodType.BLUE));
        assertThrows(IncorrectShipBoardException.class, () -> comp.getBig());
        assertThrows(IncorrectShipBoardException.class, () -> comp.getHazard());


        assertEquals(0, comp.getStoredGoods().size());
    }

    @Test
    void should_throw_exception_if_not_housing(){
        Component comp = new Drill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertThrows(IncorrectShipBoardException.class, () -> comp.addAllowedGuest(GuestType.BROWN));
        assertThrows(IncorrectShipBoardException.class, () -> comp.addGuest(GuestType.HUMAN));
        assertThrows(IncorrectShipBoardException.class, () -> comp.removeGuest());
        assertEquals(0, comp.getGuests().size());

        Component housing = new Housing(2, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);
        assertEquals(null, housing.getDirection());
        assertEquals(0, housing.getShieldDirections().size());
    }

}