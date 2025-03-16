package org.mio.progettoingsoft.advcards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.AbandonedShip;
import org.mio.progettoingsoft.advCards.Epidemic;
import org.mio.progettoingsoft.advCards.Stardust;
import org.mio.progettoingsoft.components.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdvCardsTest {
    static FlyBoard fly;
    List<Player> players;
    Player playerStefano;
    Player playerAntonio;
    Player playerLorenzo;
    Player playerAndrea;

    @BeforeEach
    public void setUp() {
        //Initialize a new match: create players and hard-coded shipboard, then load adventure cards
        fly = new FlyBoard();
        playerStefano = new Player("Stefano");
        playerAntonio = new Player("Antonio");
        playerLorenzo = new Player("Lorenzo");
        playerAndrea = new Player("Andrea");
        players = new ArrayList<Player>();
        players.add(playerStefano);
        players.add(playerAntonio);
        players.add(playerLorenzo);
        players.add(playerAndrea);

        for (int i = 0; i < 4; i++) {
            fly.addPlayer(players.get(i));
        }

        fly.getCircuit().set(6, Optional.of(fly.getScoreBoard().get(0)));
        fly.getCircuit().set(3, Optional.of(fly.getScoreBoard().get(1)));
        fly.getCircuit().set(1, Optional.of(fly.getScoreBoard().get(2)));
        fly.getCircuit().set(0, Optional.of(fly.getScoreBoard().get(3)));

        for (Player p : players) {
            assertTrue(fly.getScoreBoard().contains(p));
            assertTrue(fly.getCircuit().contains(Optional.of(p)));
        }

        for (Player p : fly.getScoreBoard()) {
            p.getShipBoard().addComponentToPosition(new Housing(1, true, HousingColor.BLUE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE), 2, 3);
            p.getShipBoard().addComponentToPosition(new Depot(2, false, false, Connector.TRIPLE, Connector.DOUBLE, Connector.FLAT, Connector.SINGLE), 2, 4);
            p.getShipBoard().addComponentToPosition(new Pipe(3, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 3, 3);
            p.getShipBoard().addComponentToPosition(new EnergyDepot(4, false, Connector.FLAT, Connector.TRIPLE, Connector.SINGLE, Connector.DOUBLE), 3, 2);
        }

        fly.getScoreBoard().stream()
                .filter(p -> p.getUsername().equals("Stefano"))
                .findFirst().ifPresent(p -> {
                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.DOUBLE, Connector.SINGLE, Connector.DOUBLE, Connector.DOUBLE), 1, 4);
                        }
                );
        // Stefano has 9 exposed connectors
        fly.getScoreBoard().stream()
                .filter(p -> p.getUsername().equals("Lorenzo"))
                .findFirst().ifPresent(p -> {
                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.FLAT, Connector.SINGLE, Connector.DOUBLE), 3, 4);
                        }
                );
        // Lorenzo has 6 exposed connectors
        fly.getScoreBoard().stream()
                .filter(p -> p.getUsername().equals("Andrea"))
                .findFirst().ifPresent(p -> {
                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.SINGLE, Connector.FLAT, Connector.SINGLE), 4, 2);
                        }
                );
        // Andrea has 8 exposed connectors
        // Antonio has 7 exposed connectors
        int stefExp = fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get().getShipBoard().getExposedConnectors();
        assertEquals(9, stefExp);
        int lorExp = fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get().getShipBoard().getExposedConnectors();
        assertEquals(6, lorExp);
        int andExp = fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get().getShipBoard().getExposedConnectors();
        assertEquals(8, andExp);
        int antExp = fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get().getShipBoard().getExposedConnectors();
        assertEquals(7, antExp);
        fly.loadAdventureCards();
        fly.shuffleDeck();
    }

    @Test
    public void should_do_nothing() {
    }

    @Test
    public void should_draw_adv_card() {
        AdventureCard card = fly.drawAdventureCard();
        assertNotNull(card);
        assertNotNull(card.getType());
    }

    @Test
    public void should_draw_adv_card_and_do_diff_based_on_type() {
        AdventureCard card = fly.drawAdventureCard();
        switch (card.getType()) {
            case PIRATE -> play_pirate(card);
            case SLAVER -> play_slaver(card);
            case PLANETS -> play_planets(card);
            case EPIDEMIC -> play_epidemic(card);
            case STARDUST -> play_stardust(card);
            case SMUGGLERS -> play_smugglers(card);
            case OPEN_SPACE -> play_open_space(card);
            case COMBAT_ZONE -> play_combat_zone(card);
            case METEOR_SWARM -> play_meteor_swarm(card);
            case ABANDONED_SHIP -> play_abandoned_ship(card);
            case ABANDONED_STATION -> play_abandoned_station(card);
            default -> throw new IllegalStateException("Unexpected value: " + card.getType());
        }
    }

    @Test
    public void should_draw_card_until_deck_is_empty(){
        while(!fly.isDeckEmpty()){
            should_draw_adv_card_and_do_diff_based_on_type();
        }
    }

    public void play_pirate(AdventureCard card){
        System.out.println("Pirate");
    }

    public void play_slaver(AdventureCard card){
        System.out.println("Slaver");
    }

    public void play_planets(AdventureCard card){
        System.out.println("Planets");
    }

    @Test
    public void should_play_epidemic(){
        AdventureCard card = new Epidemic(1, 2);
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Housing(5, Connector.TRIPLE, Connector.SINGLE, Connector.FLAT, Connector.SINGLE), 1,3);
//            p.addHumanGuest(4);
            for (int i = 0; i < 4; i++){
                p.getShipBoard().addHumanGuest();
            }
        });
        // prosegui e inserisci gli assert di verifica
        play_epidemic(card);
    }

    public void play_epidemic(AdventureCard card){
        for(Player player : fly.getScoreBoard()){
            card.startTest(fly, player);
        }
    }

    @Test
    public void should_play_stardust(){
        AdventureCard c = new Stardust(1, 2);
        play_stardust(c);
        assertEquals(16, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get())));
        assertEquals(19, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get())));
        assertEquals(20, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get())));
        assertEquals(21, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get())));
        assertEquals(3, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get()));
        assertEquals(2, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get()));
        assertEquals(1, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get()));
        assertEquals(0, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get()));

    }

    @Test
    public void should_play_stardust_twice(){
        AdventureCard c = new Stardust(1, 2);
        play_stardust(c);
        play_stardust(c);
        assertEquals(8, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get())));
        assertEquals(13, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get())));
        assertEquals(12, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get())));
        assertEquals(10, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get())));
        assertEquals(3, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get()));
        assertEquals(0, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get()));
        assertEquals(1, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get()));
        assertEquals(2, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get()));
    }

    @Test
    public void should_play_stardust_three_times() {
        AdventureCard c = new Stardust(1, 2);
        play_stardust(c);
        play_stardust(c);
        play_stardust(c);
        assertEquals(0, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get())));
        assertEquals(7, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get())));
        assertEquals(5, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get())));
        assertEquals(1, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get())));
        assertEquals(3, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get()));
        assertEquals(0, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get()));
        assertEquals(1, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get()));
        assertEquals(2, fly.getScoreBoard().indexOf(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get()));

    }

    // this method will be implemented as it is (and with the view modifying part) in the controller
    public void play_stardust(AdventureCard card){
        List<Player> playersReverse = new ArrayList<>(fly.getScoreBoard());
        Collections.reverse(playersReverse);
        for (Player p : playersReverse) {
            card.startTest(fly, p);
            // after each modification, it will be necessary to update the view one player at once
        }
    }

    public void play_smugglers(AdventureCard card){
        System.out.println("Smugglers");
    }

    public void play_open_space(AdventureCard card){
        System.out.println("Open space");
    }

    public void play_combat_zone(AdventureCard card){
        System.out.println("Combat zone");
    }

    public void play_meteor_swarm(AdventureCard card){
        System.out.println("Meteor swarm");
    }

    @Test
    public void setUpAbandondShip(){
        Component firstHousing = new Housing(4, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE);
        Component secondHousing = new Housing(5, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE, Connector.TRIPLE);

        playerStefano.getShipBoard().addComponentToPosition(firstHousing, 3, 5);
        playerStefano.getShipBoard().addComponentToPosition(secondHousing, 1, 5);

        playerAntonio.getShipBoard().addComponentToPosition(firstHousing, 3, 5);

        firstHousing.addHumanMember();
        firstHousing.addHumanMember();

        secondHousing.addHumanMember();
        secondHousing.addHumanMember();

        assertEquals(2, firstHousing.getQuantityGuests());
        assertEquals(2, secondHousing.getQuantityGuests());
    }

    public void play_abandoned_ship(AdventureCard card){
        card.start(fly);
    }

    public void play_abandoned_station(AdventureCard card){
        System.out.println("Abandoned station");
    }

    public static void main(String[] args) {
        AdvCardsTest provaTest = new AdvCardsTest();
        provaTest.setUp();
        provaTest.setUpAbandondShip();

        AdventureCard abandShip = new AbandonedShip(1, 1, 2, 2, 3);

        abandShip.start(fly);

    }


}
