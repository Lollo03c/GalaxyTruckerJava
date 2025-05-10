//package org.mio.progettoingsoft;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mio.progettoingsoft.components.*;
//import org.mio.progettoingsoft.exceptions.CannotAddPlayerException;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class    AdvCardsTest {
//    static FlyBoard fly;
//    List<Player> players;
//    Player playerStefano;
//    Player playerAntonio;
//    Player playerLorenzo;
//    Player playerAndrea;
//
//    @BeforeEach
//    public void setUp() throws CannotAddPlayerException {
//        //Initialize a new match: create players and hard-coded shipboard, then load adventure cards
//        fly = new FlyBoard();
//        playerStefano = new Player("Stefano", HousingColor.BLUE);
//        playerAntonio = new Player("Antonio", HousingColor.YELLOW);
//        playerLorenzo = new Player("Lorenzo", HousingColor.GREEN);
//        playerAndrea = new Player("Andrea", HousingColor.RED);
//        players = new ArrayList<Player>();
//        players.add(playerStefano);
//        players.add(playerAntonio);
//        players.add(playerLorenzo);
//        players.add(playerAndrea);
//
//        for (int i = 0; i < 4; i++) {
//            fly.addPlayer(players.get(i).getNickname(), players.get(i).getColor());
//        }
//
//        fly.getCircuit().set(6, Optional.of(fly.getScoreBoard().get(0)));
//        fly.getCircuit().set(3, Optional.of(fly.getScoreBoard().get(1)));
//        fly.getCircuit().set(1, Optional.of(fly.getScoreBoard().get(2)));
//        fly.getCircuit().set(0, Optional.of(fly.getScoreBoard().get(3)));
//
//        for (Player p : players) {
//            assertTrue(fly.getScoreBoard().contains(p));
//            assertTrue(fly.getCircuit().contains(Optional.of(p)));
//        }
//
//        for (Player p : fly.getScoreBoard()) {
//            p.getShipBoard().addComponentToPosition(new Depot(2, false, false, Connector.TRIPLE, Connector.DOUBLE, Connector.FLAT, Connector.SINGLE), 2, 4);
//            p.getShipBoard().addComponentToPosition(new Pipe(3, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 3, 3);
//            p.getShipBoard().addComponentToPosition(new EnergyDepot(4, false, Connector.FLAT, Connector.TRIPLE, Connector.SINGLE, Connector.DOUBLE), 3, 2);
//        }
//
//        fly.getScoreBoard().stream()
//                .filter(p -> p.getNickname().equals("Stefano"))
//                .findFirst().ifPresent(p -> {
//                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.DOUBLE, Connector.SINGLE, Connector.DOUBLE, Connector.DOUBLE), 1, 4);
//                        }
//                );
//        // Stefano has 9 exposed connectors
//        fly.getScoreBoard().stream()
//                .filter(p -> p.getNickname().equals("Lorenzo"))
//                .findFirst().ifPresent(p -> {
//                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.FLAT, Connector.SINGLE, Connector.DOUBLE), 3, 4);
//                        }
//                );
//        // Lorenzo has 6 exposed connectors
//        fly.getScoreBoard().stream()
//                .filter(p -> p.getNickname().equals("Andrea"))
//                .findFirst().ifPresent(p -> {
//                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.SINGLE, Connector.FLAT, Connector.SINGLE), 4, 2);
//                        }
//                );
//        // Andrea has 8 exposed connectors
//        // Antonio has 7 exposed connectors
//        int stefExp = fly.getScoreBoard().stream().filter(p -> p.getNickname().equals("Stefano")).findFirst().get().getShipBoard().getExposedConnectors();
//        assertEquals(9, stefExp);
//        int lorExp = fly.getScoreBoard().stream().filter(p -> p.getNickname().equals("Lorenzo")).findFirst().get().getShipBoard().getExposedConnectors();
//        assertEquals(6, lorExp);
//        int andExp = fly.getScoreBoard().stream().filter(p -> p.getNickname().equals("Andrea")).findFirst().get().getShipBoard().getExposedConnectors();
//        assertEquals(8, andExp);
//        int antExp = fly.getScoreBoard().stream().filter(p -> p.getNickname().equals("Antonio")).findFirst().get().getShipBoard().getExposedConnectors();
//        assertEquals(7, antExp);
//        fly.loadAdventureCards();
//        fly.shuffleDeck();
//    }
//
//    @Test
//    public void should_do_nothing() {
//    }
//
//    @Test
//    public void should_draw_adv_card() {
//        AdventureCard card = fly.drawAdventureCard();
//        assertNotNull(card);
//        assertNotNull(card.getType());
//    }
//
//    @Test
//    public void should_draw_card_until_deck_is_empty() {
//        while (!fly.isDeckEmpty()) {
//            should_draw_adv_card();
//        }
//    }
//
//}
