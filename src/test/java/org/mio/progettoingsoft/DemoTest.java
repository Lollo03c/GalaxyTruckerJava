//package org.mio.progettoingsoft;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mio.progettoingsoft.components.HousingColor;
//import org.mio.progettoingsoft.exceptions.NoMoreComponentsException;
//
//import java.util.Map;
//
//public class DemoTest {
//    FlyBoard flyBoard;
//    Map<String, HousingColor> users = Map.of(
//            "Stefano", HousingColor.BLUE,
//            "Andrea", HousingColor.RED);
//
//    @BeforeEach
//    public void setUp() {
//        flyBoard = new FlyBoard();
//        for (String user : users.keySet()) {
//            flyBoard.addPlayer(user, users.get(user));
//        }
//        while (true) {
//            try {
//                flyBoard.addUncoveredComponent(flyBoard.drawComponent());
//            } catch (NoMoreComponentsException e) {
//                break;
//            }
//        }
//    }
//
//    @Test
//    public void assembleShip() {
//        // Player 1: Stefano
//        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(95), 3, 3);
//        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(133), 1, 3);
//        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(10), 2, 2);
//        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(37), 2, 4);
//        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(141), 2, 5);
//        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(151), 3, 4);
//        flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(28), 1, 4);
//
//        System.out.println("Incorrect components: " + flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().getIncorrectComponents().toString());
//        System.out.println("Number of parts: " + flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().getMultiplePieces().size());
//
//        System.out.println(flyBoard.getPlayerByUsername("Stefano").get().getShipBoard().toString());
//
//        // Player 2: Andrea
//        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(67), 3, 3);
//        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(156), 1, 3);
//        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(125), 2, 2);
//        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(9), 2, 4);
//        flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().addComponentToPosition(
//                flyBoard.chooseComponentFromUncoveredById(84), 3, 2);
//
//        //System.out.println("Incorrect components: " + flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().getIncorrectComponents().toString());
//        //System.out.println("Number of parts: " + flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().getMultiplePieces().size());
//
//        System.out.println(flyBoard.getPlayerByUsername("Andrea").get().getShipBoard().toString());
//    }
//}
