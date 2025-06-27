package org.mio.progettoingsoft.components;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.Component;
import org.mio.progettoingsoft.model.components.Shield;
import org.mio.progettoingsoft.model.enums.Connector;
import org.mio.progettoingsoft.model.enums.Direction;

import static org.junit.jupiter.api.Assertions.*;

class ShieldTest {

    @Test
    void should_rotateClockwise_0() {
        Component shield = new Shield(1, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.FLAT);
        shield.rotate(0);

        assertTrue(shield.getShieldDirections().contains(Direction.FRONT));
        assertTrue(shield.getShieldDirections().contains(Direction.RIGHT));
    }

    @Test
    void should_rotateClockwise_1() {
        Component shield = new Shield(1, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.FLAT);
        shield.rotate(1);

        assertTrue(shield.getShieldDirections().contains(Direction.RIGHT));
        assertTrue(shield.getShieldDirections().contains(Direction.BACK));
    }

    @Test
    void should_rotateClockwise_2() {
        Component shield = new Shield(1, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.FLAT);
        shield.rotate(2);

        assertTrue(shield.getShieldDirections().contains(Direction.BACK));
        assertTrue(shield.getShieldDirections().contains(Direction.LEFT));
    }

    @Test
    void should_rotateClockwise_3() {
        Component shield = new Shield(1, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.FLAT);
        shield.rotate(3);

        assertTrue(shield.getShieldDirections().contains(Direction.LEFT));
        assertTrue(shield.getShieldDirections().contains(Direction.FRONT));
    }
}