package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.Direction;

import static org.junit.jupiter.api.Assertions.*;

class DoubleEngineTest {

    @Test
    void should_rotate_0(){
        Component engine = new DoubleEngine(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.BACK, engine.getDirection());
        engine.rotate(0);
        assertEquals(Direction.BACK, engine.getDirection());
        System.out.println(engine.toString());

        assertEquals(2, engine.getEnginePower(true));
        assertEquals(0, engine.getEnginePower(false));
    }

    @Test
    void should_rotate_1(){
        Component engine = new DoubleEngine(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.BACK, engine.getDirection());
        engine.rotate(1);
        assertEquals(Direction.LEFT, engine.getDirection());
    }


    @Test
    void should_rotate_2(){
        Component engine = new DoubleEngine(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.BACK, engine.getDirection());
        engine.rotate(2);
        assertEquals(Direction.FRONT, engine.getDirection());
    }


    @Test
    void should_rotate_3(){
        Component engine = new DoubleEngine(1, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Direction.BACK, engine.getDirection());
        engine.rotate(3);
        assertEquals(Direction.RIGHT, engine.getDirection());
    }

}