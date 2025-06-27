package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.Component;
import org.mio.progettoingsoft.model.components.DoubleDrill;
import org.mio.progettoingsoft.model.enums.Connector;
import org.mio.progettoingsoft.model.enums.Direction;

import static org.junit.jupiter.api.Assertions.*;

class DoubleDrillTest {

    @Test
    void should_rotate_0(){
        Component doubleDrill = new DoubleDrill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.FRONT, doubleDrill.getDirection());

        doubleDrill.rotate(0);

        assertEquals(Direction.FRONT, doubleDrill.getDirection());
        assertEquals(2, doubleDrill.getFirePower(true));
        assertEquals(0, doubleDrill.getFirePower(false));
    }

    @Test
    void should_rotate_1(){
        Component doubleDrill = new DoubleDrill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.FRONT, doubleDrill.getDirection());

        doubleDrill.rotate(1);

        assertEquals(Direction.RIGHT, doubleDrill.getDirection());
        assertEquals(1, doubleDrill.getFirePower(true));
        assertEquals(0, doubleDrill.getFirePower(false));
    }

    @Test
    void should_rotate_2(){
        Component doubleDrill = new DoubleDrill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.FRONT, doubleDrill.getDirection());

        doubleDrill.rotate(2);

        assertEquals(Direction.BACK, doubleDrill.getDirection());
        assertEquals(1, doubleDrill.getFirePower(true));
        assertEquals(0, doubleDrill.getFirePower(false));
    }

    @Test
    void should_rotate_3(){
        Component doubleDrill = new DoubleDrill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.FRONT, doubleDrill.getDirection());

        doubleDrill.rotate(3);

        assertEquals(Direction.LEFT, doubleDrill.getDirection());
        assertEquals(1, doubleDrill.getFirePower(true));
        assertEquals(0, doubleDrill.getFirePower(false));
    }

}