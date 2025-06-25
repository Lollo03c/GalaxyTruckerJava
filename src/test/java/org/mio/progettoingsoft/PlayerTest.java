package org.mio.progettoingsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.ShipBoardNormal;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class PlayerTest {

    FlyBoard flyBoard;
    Player antonio;

    @BeforeEach
    public void setup(){
        Set<String> nicks = new HashSet<>();
        nicks.addAll(Set.of(
                "Antonio",
                "Lorenzo",
                "Andrea",
                "Stefano"
        ));

        flyBoard = new FlyBoardNormal(nicks);
        antonio = flyBoard.getPlayerByUsername("Antonio");
    }
    @Test
    void should_create_a_player(){
        assertEquals("Antonio", antonio.getNickname());
        ShipBoard ship = antonio.getShipBoard();

        assertEquals(antonio.getColor(), ship.getHousingColor());
    }

    @Test
    void should_compare_players(){
        Player stefano = flyBoard.getPlayerByUsername("Stefano");

        assertNotEquals(null, antonio);
        assertNotEquals(antonio, stefano);

        Player second = flyBoard.getPlayerByUsername("Antonio");
        assertEquals(antonio, second);

        ShipBoard ship = ShipBoardNormal.buildBlue(flyBoard);
        antonio.setShipBoard(ship);
    }

    @Test
    void should_manage_credits(){
        assertEquals(0, antonio.getCredits());
        antonio.addCredits(5);
        assertEquals(5, antonio.getCredits());
        antonio.removeCredits(3);
        assertEquals(2, antonio.getCredits());
    }

    @Test
    void should_manage_running(){


        assertFalse(antonio.isRunning());
        antonio.setRunning(true);
        assertTrue(antonio.isRunning());
    }
  
}