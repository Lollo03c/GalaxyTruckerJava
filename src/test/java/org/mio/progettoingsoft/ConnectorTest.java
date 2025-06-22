package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTest {

    @Test
    void should_connect_triple_with_triple(){
        assertTrue(Connector.TRIPLE.isCompatible(Connector.TRIPLE));
    }

    @Test
    void should_connect_triple_with_double(){
        assertTrue(Connector.TRIPLE.isCompatible(Connector.DOUBLE));
    }

    @Test
    void should_connect_triple_with_single(){
        assertTrue(Connector.TRIPLE.isCompatible(Connector.SINGLE));
    }

    @Test
    void should_not_connect_triple_with_flat(){
        assertFalse(Connector.TRIPLE.isCompatible(Connector.FLAT));
    }
    //END TRIPLE

    @Test
    void should_connect_double_with_triple(){
        assertTrue(Connector.DOUBLE.isCompatible(Connector.TRIPLE));
    }

    @Test
    void should_connect_double_with_double(){
        assertTrue(Connector.DOUBLE.isCompatible(Connector.DOUBLE));
    }

    @Test
    void should_not_connect_double_with_single(){
        assertFalse(Connector.DOUBLE.isCompatible(Connector.SINGLE));
    }

    @Test
    void should_not_connect_double_with_flat(){
        assertFalse(Connector.DOUBLE.isCompatible(Connector.FLAT));
    }
    //END DOUBLE

    void should_connect_single_with_triple(){
        assertTrue(Connector.SINGLE.isCompatible(Connector.TRIPLE));
    }

    @Test
    void should_not_connect_single_with_double(){
        assertFalse(Connector.SINGLE.isCompatible(Connector.DOUBLE));
    }

    @Test
    void should_connect_single_with_single(){
        assertTrue(Connector.SINGLE.isCompatible(Connector.SINGLE));
    }

    @Test
    void should_not_connect_single_with_flat(){
        assertFalse(Connector.SINGLE.isCompatible(Connector.FLAT));
    }
    //END SINGLE

    @Test
    void should_not_connect_flat_with_triple(){
        assertFalse(Connector.FLAT.isCompatible(Connector.TRIPLE));
    }

    @Test
    void should_not_connect_flat_with_double(){
        assertFalse(Connector.FLAT.isCompatible(Connector.DOUBLE));
    }

    @Test
    void should_not_connect_flat_with_single(){
        assertFalse(Connector.FLAT.isCompatible(Connector.SINGLE));
    }

    @Test
    void should_connect_flat_with_flat(){
        assertTrue(Connector.FLAT.isCompatible(Connector.FLAT));
    }

}