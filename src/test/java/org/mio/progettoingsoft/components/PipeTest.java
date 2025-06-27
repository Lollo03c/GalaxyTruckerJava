package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.Component;
import org.mio.progettoingsoft.model.components.Pipe;
import org.mio.progettoingsoft.model.enums.Connector;
import org.mio.progettoingsoft.model.enums.Direction;

import static org.junit.jupiter.api.Assertions.*;

class PipeTest {

    @Test
    void should_create_a_pipe(){
        Component pipe = new Pipe(5, Connector.TRIPLE, Connector.DOUBLE, Connector.SINGLE, Connector.FLAT);

        assertEquals(Connector.TRIPLE, pipe.getConnector(Direction.FRONT));
        assertEquals(Connector.DOUBLE, pipe.getConnector(Direction.BACK));
        assertEquals(Connector.SINGLE, pipe.getConnector(Direction.RIGHT));
        assertEquals(Connector.FLAT, pipe.getConnector(Direction.LEFT));
    }
}