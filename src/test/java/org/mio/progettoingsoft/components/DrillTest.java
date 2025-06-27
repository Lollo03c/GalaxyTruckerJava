package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.Component;
import org.mio.progettoingsoft.model.components.Drill;
import org.mio.progettoingsoft.model.enums.Connector;
import org.mio.progettoingsoft.model.enums.Direction;

import static org.junit.jupiter.api.Assertions.*;

class DrillTest {

    @Test
    void should_rotate_0(){
        Component drill = new Drill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.FRONT, drill.getDirection());

        drill.rotate(0);

        assertEquals(1, drill.getFirePower(true));
        assertEquals(1, drill.getFirePower(false));
        assertEquals(Direction.FRONT, drill.getDirection());
    }

    @Test
    void should_rotate_1(){
        Component drill = new Drill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        drill.rotate(1);

        assertEquals(0.5, drill.getFirePower(true));
        assertEquals(0.5, drill.getFirePower(false));
        assertEquals(Direction.RIGHT, drill.getDirection());
    }

    @Test
    void should_rotate_2(){
        Component drill = new Drill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        drill.rotate(2);

        assertEquals(0.5, drill.getFirePower(true));
        assertEquals(0.5, drill.getFirePower(false));
        assertEquals(Direction.BACK, drill.getDirection());
    }

    @Test
    void should_rotate_3(){
        Component drill = new Drill(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        drill.rotate(3);

        assertEquals(0.5, drill.getFirePower(true));
        assertEquals(0.5, drill.getFirePower(false));
        assertEquals(Direction.LEFT, drill.getDirection());
    }

}