package org.mio.progettoingsoft.advcards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.components.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvCardsTest {
    FlyBoard fly;
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
            fly.getCircuit().set(3 - i, Optional.of(players.get(i)));
        }

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
        fly.getScoreBoard().stream()
                .filter(p -> p.getUsername().equals("Lorenzo"))
                .findFirst().ifPresent(p -> {
                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.FLAT, Connector.SINGLE, Connector.DOUBLE), 3, 4);
                        }
                );
        fly.getScoreBoard().stream()
                .filter(p -> p.getUsername().equals("Andrea"))
                .findFirst().ifPresent(p -> {
                            p.getShipBoard().addComponentToPosition(new Pipe(5, Connector.TRIPLE, Connector.SINGLE, Connector.FLAT, Connector.SINGLE), 4, 2);
                        }
                );
        fly.loadAdventureCards();
        fly.shuffleDeck();
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

    public void play_epidemic(AdventureCard card){
        System.out.println("Epidemic");
    }

    public void play_stardust(AdventureCard card){
        for (Player p : players) {
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

    public void play_abandoned_ship(AdventureCard card){
        System.out.println("Abandoned ship");
    }

    public void play_abandoned_station(AdventureCard card){
        System.out.println("Abandoned station");
    }
}
