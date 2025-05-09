package org.mio.progettoingsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.advCards.sealed.SldAbandonedShip;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.components.AlienType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.NoMoreComponentsException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

        addAlien(flyBoard.getPlayerByUsername("Stefano").get().getShipBoard(), 2, 4, AlienType.BROWN);
        addHuman(flyBoard.getPlayerByUsername("Stefano").get().getShipBoard(), 2, 3);
        addHuman(flyBoard.getPlayerByUsername("Stefano").get().getShipBoard(), 2, 3);

        addHuman(flyBoard.getPlayerByUsername("Andrea").get().getShipBoard(), 2, 3);
        addHuman(flyBoard.getPlayerByUsername("Andrea").get().getShipBoard(), 2, 3);

        addHuman(flyBoard.getPlayerByUsername("Antonio").get().getShipBoard(), 2, 3);
        addHuman(flyBoard.getPlayerByUsername("Antonio").get().getShipBoard(), 2, 3);
        addAlien(flyBoard.getPlayerByUsername("Antonio").get().getShipBoard(), 3, 2, AlienType.PURPLE);
        addHuman(flyBoard.getPlayerByUsername("Antonio").get().getShipBoard(), 4, 2);
        addHuman(flyBoard.getPlayerByUsername("Antonio").get().getShipBoard(), 4, 2);

        addHuman(flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard(), 2, 3);
        addHuman(flyBoard.getPlayerByUsername("Lorenzo").get().getShipBoard(), 2, 3);

        flyBoard.addPlayerToCircuit("Antonio", 1);
        flyBoard.addPlayerToCircuit("Lorenzo", 0);
        flyBoard.addPlayerToCircuit("Stefano", 6);
        flyBoard.addPlayerToCircuit("Andrea", 3);


        assertEquals(0, flyBoard.getScoreBoard().indexOf(flyBoard.getPlayerByUsername("Stefano").get()));
        assertEquals(1, flyBoard.getScoreBoard().indexOf(flyBoard.getPlayerByUsername("Andrea").get()));
        assertEquals(2, flyBoard.getScoreBoard().indexOf(flyBoard.getPlayerByUsername("Antonio").get()));
        assertEquals(3, flyBoard.getScoreBoard().indexOf(flyBoard.getPlayerByUsername("Lorenzo").get()));
        assertTrue(flyBoard.getPlayerByUsername("Stefano").get().isRunning());
        assertTrue(flyBoard.getPlayerByUsername("Andrea").get().isRunning());
        assertTrue(flyBoard.getPlayerByUsername("Antonio").get().isRunning());
        assertTrue(flyBoard.getPlayerByUsername("Lorenzo").get().isRunning());
    }

    private void addHuman(ShipBoard ship, int row, int col){
        if(!ship.getComponent(row,col).addHumanMember())
            throw new RuntimeException("Can't add human");
    }

    private void addAlien(ShipBoard ship, int row, int col, AlienType alienType){
        if(!ship.getComponent(row, col).addAlien(alienType)){
            throw new RuntimeException("Can't add alien");
        }
    }

    @Test
    public void should_play_abandoned_ship(){
        flyBoard.setState(GameState.DRAW_CARD);
        SldAdvCard card = new SldAbandonedShip(1, 2, 3, 4, 1);
        switch (card){
            case SldAbandonedShip c ->{
                /* test sequence */
                flyBoard.setState(GameState.CARD_EFFECT);
                assertThrows(IllegalStateException.class, ()->{
                    c.init(flyBoard);
                });
                flyBoard.setState(GameState.DRAW_CARD);
                c.init(flyBoard);
                assertEquals(GameState.CARD_EFFECT, flyBoard.getState());
                assertEquals(CardState.CREW_REMOVE_CHOICE, c.getState());

                // Antonio is not the leader
                assertThrows(BadParameterException.class, ()->{
                    c.applyEffect(flyBoard, flyBoard.getPlayerByUsername("Antonio").get(), true, null);
                });
                // Stefano is the leader but the list is empty
                assertThrows(BadParameterException.class, ()->{
                    c.applyEffect(flyBoard, flyBoard.getPlayerByUsername("Stefano").get(), true, null);
                });
                // Stefano is the leader but doesn't want to apply effect
                c.applyEffect(flyBoard, flyBoard.getPlayerByUsername("Stefano").get(), false, Collections.EMPTY_LIST);
                assertNotEquals(CardState.FINALIZED, c.getState());
                // Andrea is the next player and doesn't want to apply
                c.applyEffect(flyBoard, flyBoard.getPlayerByUsername("Andrea").get(), false, Collections.EMPTY_LIST);
                assertNotEquals(CardState.FINALIZED, c.getState());
                List<Integer[]> housings = new ArrayList<>();
                housings.add(new Integer[]{2,3});
                c.applyEffect(flyBoard, flyBoard.getPlayerByUsername("Antonio").get(), true, housings);
                assertEquals(CardState.FINALIZED, c.getState());
                assertEquals(4, flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().getQuantityGuests());
                assertEquals(1, flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().getComponent(2,3).getNumHumanMembers());
                assertEquals(2, flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().getComponent(4,2).getNumHumanMembers());
                assertEquals(true, flyBoard.getPlayerByUsername("Antonio").get().getShipBoard().getComponent(3,2).containsAlien(AlienType.PURPLE));
                c.finish(flyBoard);
                assertEquals(GameState.DRAW_CARD, flyBoard.getState());
            }
            default -> throw new IllegalStateException("Unexpected value: " + card);
        }
    }
}
