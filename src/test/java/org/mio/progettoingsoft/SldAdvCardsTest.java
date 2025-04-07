package org.mio.progettoingsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.NoMoreComponentsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SldAdvCardsTest {
    private FlyBoard flyBoard;
    Map<String, HousingColor> users = Map.of(
            "Stefano", HousingColor.BLUE,
            "Andrea", HousingColor.RED,
            "Antonio", HousingColor.GREEN,
            "Lorenzo", HousingColor.YELLOW
    );

    @BeforeEach
    public void init (){
        flyBoard = new FlyBoard();
        for (String user : users.keySet()) {
            flyBoard.addPlayer(user, users.get(user));
        }
        while (true) {
            try {
                flyBoard.addUncoveredComponent(flyBoard.drawComponent());
            } catch (NoMoreComponentsException e) {
                break;
            }
        }
        // Player 1: Stefano
        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(95), 3, 3,0);
        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(133), 1, 3,0);
        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(10), 2, 2,0);
        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(37), 2, 4,0);
        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(141), 2, 5,0);
        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(151), 3, 4,0);
        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(28), 1, 4,0);

        // Player 2: Andrea
        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(67), 3, 3,0);
        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(156), 1, 3,0);
        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(125), 2, 2,0);
        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(9), 2, 4,0);
        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(84), 3, 2,0);

        // Player 3: Antonio
        flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(60), 2, 4,0);
        flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(63), 3, 1,0);
        flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(148), 3, 3,0);
        flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(43), 3, 2,0);
        flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(50), 4, 2,0);
        flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(80), 4, 1,0);
        flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(134), 1, 3,0);
        flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(13), 2, 2,0);

        // Player 4: Lorenzo
        flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(27), 2, 4,0);
        flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(98), 3, 3,0);
        flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(2), 3, 2,0);
        flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(107), 2, 2,0);
        flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(153), 1, 3,0);
        flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().addRotatedComponentToPosition(
                flyBoard.chooseComponentFromUncoveredById(135), 1, 4,0);

        assertEquals(0, flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().getIncorrectComponents().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().getIncorrectComponents().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().getIncorrectComponents().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().getIncorrectComponents().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().getIncorrectDrills().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().getIncorrectDrills().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().getIncorrectDrills().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().getIncorrectDrills().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().getIncorrectEngines().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().getIncorrectEngines().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().getIncorrectEngines().size());
        assertEquals(0, flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().getIncorrectEngines().size());
        assertEquals(1, flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().getMultiplePieces().size());
        assertEquals(1, flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().getMultiplePieces().size());
        assertEquals(1, flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().getMultiplePieces().size());
        assertEquals(1, flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard().getMultiplePieces().size());
    }
}
