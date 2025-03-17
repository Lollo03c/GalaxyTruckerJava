package org.mio.progettoingsoft;

import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.exceptions.BadCardException;
import org.mio.progettoingsoft.exceptions.NoPowerException;

import java.util.*;

public class Controller {
    private final FlyBoard flyBoard;

    public Controller(FlyBoard board) {
        this.flyBoard = board;
    }

    public void drawCardAndApply() {
        AdventureCard card = flyBoard.drawAdventureCard();
        switch (card.getType()) {
            case PIRATE -> play_pirate(card);
            case SLAVER -> play_slaver(card);
            case PLANETS -> play_planets(card);
            case EPIDEMIC -> play_epidemic(card);  // WORKS
            case STARDUST -> play_stardust(card);  // WORKS
            case SMUGGLERS -> play_smugglers(card);
            case OPEN_SPACE -> play_open_space(card); // WORKS
            case COMBAT_ZONE -> play_combat_zone(card);
            case METEOR_SWARM -> play_meteor_swarm(card);
            case ABANDONED_SHIP -> play_abandoned_ship(card);
            case ABANDONED_STATION -> play_abandoned_station(card);
            default -> throw new IllegalStateException("Unexpected value: " + card.getType());
        }
    }

    private void play_epidemic(AdventureCard card) {
        for (Player player : flyBoard.getScoreBoard()) {
            List<Component> toDoRemove = new ArrayList<>();
            player.getShipBoard().getComponentsStream()
                    .filter(c -> c.getType().equals(ComponentType.HOUSING))
                    .forEach(c -> {
                        Map<Direction, Component> adj = player.getShipBoard().getAdjacent(c.getRow(), c.getColumn());
                        adj.forEach((direction, component) -> {
                            if (component.getType().equals(ComponentType.HOUSING) && c.getQuantityGuests() > 0 && component.getQuantityGuests() > 0) {
                                toDoRemove.add(component);
                            }
                        });
                    });
            for (Component c : toDoRemove) {
                c.removeGuest();
            }
        }
    }

    private void play_stardust(AdventureCard card) {
        List<Player> playersReverse = new ArrayList<>(flyBoard.getScoreBoard());
        Collections.reverse(playersReverse);
        for (Player player : playersReverse) {
            int daysLost = player.getShipBoard().getExposedConnectors();
            flyBoard.moveDays(player, -daysLost);
        }
    }

    private void play_open_space(AdventureCard card) {
        for (Player player : flyBoard.getScoreBoard()) {
            int toActivate = player.getView().askDoubleEngine();
            player.getShipBoard().removeEnergy(toActivate);
            int base = player.getShipBoard().getBaseEnginePower();
            int power = base + toActivate * 2;
            if (power == 0) {
                throw new NoPowerException(player);
            } else {
                flyBoard.moveDays(player, power);
            }
        }
    }

    public void meteorSwarmController(AdventureCard card) {
        if (!card.getType().equals(AdvCardType.METEOR_SWARM))
            throw new BadCardException("");

        List<Meteor> meteors = card.getMeteors();

        List<Player> score = new ArrayList<>(flyBoard.getScoreBoard());
        int offsetRow = score.getFirst().getShipBoard().getOffsetRow();
        int offsetCol = score.getFirst().getShipBoard().getOffsetCol();

        for (Meteor meteor : meteors) {
            int row = score.getFirst().getView().rollDicesAndSum() - offsetRow;
            int col = score.getFirst().getView().rollDicesAndSum() - offsetCol;

            for (Player player : score) {
                if (meteor.getDirection().equals(Direction.BACK) || meteor.getDirection().equals(Direction.FRONT))
                    meteor.hit(player, col);
                else
                    meteor.hit(player, row);
            }
        }
    }

    public void smugglersController(AdventureCard card) {
        if (!card.getType().equals(AdvCardType.SMUGGLERS))
            throw new BadCardException("");

        boolean ended = false;
        int daysLost = card.getDaysLost();
        int strength = card.getStrenght();
        List<GoodType> goods = card.getGoods();


        List<Player> playerList = new ArrayList<>(flyBoard.getScoreBoard());
        for (Player player : playerList) {
            List<Component> doubleDrills = player.getView().askDoubleDrill();

            float power = player.getShipBoard().getBaseEnginePower();
            for (Component drill : doubleDrills) {
                power += drill.getFirePower();
            }
            player.getShipBoard().removeEnergy(doubleDrills.size());

            if (power > strength) {
                boolean answer = player.getView().askForEffect(card.getType());

                if (answer) {
                    flyBoard.moveDays(player, -daysLost);
                    for (GoodType type : goods) {
                        Component depot = player.getView().askForDepotToAdd(type);
                        depot.addGood(type);
                    }
                }
                break;
            } else if (power < strength) {
                player.getShipBoard().stoleGood();
            }
        }
    }

}

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
            case EPIDEMIC -> play_epidemic(card);  // WORKS
            case STARDUST -> play_stardust(card);  // WORKS
            case SMUGGLERS -> play_smugglers(card);
            case OPEN_SPACE -> play_open_space(card); // WORKS
            case COMBAT_ZONE -> play_combat_zone(card);
            case METEOR_SWARM -> play_meteor_swarm(card);
            case ABANDONED_SHIP -> play_abandoned_ship(card);
            case ABANDONED_STATION -> play_abandoned_station(card);
            default -> throw new IllegalStateException("Unexpected value: " + card.getType());
        }
    }

    @Test
    public void should_draw_card_until_deck_is_empty() {
        while (!fly.isDeckEmpty()) {
            should_draw_adv_card_and_do_diff_based_on_type();
        }
    }

    public void play_pirate(AdventureCard card) {
        System.out.println("Pirate");
    }

    public void play_slaver(AdventureCard card) {
        // DEVONO ESSERE APPLICATE UNO ALLA VOLTA
        /*
        (Player p : fly.getScoreBoard()) {
            int numDoubleDrills = (int) p.getShipBoard().getComponentsStream().filter(c -> c.getType().equals(ComponentType.DOUBLE_DRILL)).count();
            int numBatteries = p.getShipBoard().getQuantBatteries();
            if (!card.canBeDefeatedBy(p)) {
                System.out.println(p.getUsername() + " la tua potenza di base (" + p.getShipBoard().getBaseFirePower() + " non è sufficiente per sconfiggere gli schiavisti");
                if (numDoubleDrills > 0 || numBatteries <= 0) {
                    System.out.println("Hai " + numDoubleDrills + " cannoni doppi e " + numBatteries + " batterie:");
                    List<Component> doubles = p.getShipBoard().getComponentsStream().filter(c -> c.getType().equals(ComponentType.DOUBLE_DRILL)).toList();
                    boolean[] activations = new boolean[doubles.size()];
                    int i;
                    for(i = 0; i < doubles.size(); i++) {
                        activations[i] = false;
                    }
                    for (i = 0; i < doubles.size() && numBatteries > 0; i++) {
                        System.out.println(i + " - Direzione:" + doubles.get(i).getDirection() + ". Desideri attivarlo?[s/n]");
                        //Variable to be modified for testing
                        activations[i] = true;
                        if (activations[i]) {
                            numBatteries--;
                            p.getShipBoard().removeEnergy();
                        }
                    }
                    if(numBatteries == 0 && i < doubles.size())
                        System.out.println("Hai terminato le batterie");
                    for (i = 0; i < doubles.size(); i++) {
                        if (activations[i]) {
                            doubles.get(i).setActive(true);
                        }
                    }
                } else {
                    System.out.println("Non hai doppi cannoni da attivare oppure non hai nessun segnalino batteria");
                }
            }
        }*/
        System.out.println("Slaver");

        boolean defeated = false;
        for (int i = 0; i < fly.getScoreBoard().size() && !defeated; i++) {
            int numDoubleDrills = (int) fly.getScoreBoard().get(0).getShipBoard().getComponentsStream().filter(c -> c.getType().equals(ComponentType.DOUBLE_DRILL)).count();
            int numBatteries = fly.getScoreBoard().get(0).getShipBoard().getQuantBatteries();

        }
    }

    public void play_planets(AdventureCard card) {
        System.out.println("Planets");
    }

    @Test
    public void should_play_epidemic() {
        AdventureCard card = new Epidemic(1, 2);
        epidemic_setup();
        play_epidemic(card);
        assertEquals(2, fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get().getShipBoard().getQuantityGuests());
        assertEquals(2, fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get().getShipBoard().getQuantityGuests());
        assertEquals(1, fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get().getShipBoard().getQuantityGuests());
        assertEquals(2, fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get().getShipBoard().getQuantityGuests());
    }

    @Test
    public void should_play_epidemic_twice() {
        AdventureCard card = new Epidemic(1, 2);
        epidemic_setup();
        play_epidemic(card);
        play_epidemic(card);
        assertEquals(0, fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get().getShipBoard().getQuantityGuests());
        assertEquals(2, fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get().getShipBoard().getQuantityGuests());
        assertEquals(1, fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get().getShipBoard().getQuantityGuests());
        assertEquals(2, fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get().getShipBoard().getQuantityGuests());
    }

    public void epidemic_setup() {
        // add a housing connected to the main housing and insert 4 human crew members
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Housing(5, Connector.TRIPLE, Connector.SINGLE, Connector.DOUBLE, Connector.SINGLE), 1, 3);
            for (int i = 0; i < 4; i++) {
                p.getShipBoard().addHumanGuest();
            }
        });
        // doesn't add any housing and insert 2 human members
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().ifPresent(p -> {
            for (int i = 0; i < 2; i++) {
                p.getShipBoard().addHumanGuest();
            }
        });
        // adds 2 humans in main housing, adds a housing and a alien support, then adds an alien in the new housing
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Housing(5, Connector.TRIPLE, Connector.SINGLE, Connector.DOUBLE, Connector.SINGLE), 1, 3);
            p.getShipBoard().addComponentToPosition(new AlienHousing(6, AlienType.BROWN, Connector.TRIPLE, Connector.SINGLE, Connector.SINGLE, Connector.SINGLE), 1, 2);
            for (int i = 0; i < 2; i++) {
                p.getShipBoard().getComponentsStream().filter(c -> c.getId() == 1).findFirst().ifPresent(c -> {
                    c.addHumanMember();
                    c.addHumanMember();
                });
            }
            p.getShipBoard().getComponentsStream().filter(c -> c.getId() == 5).findFirst()
                    .ifPresentOrElse(c -> {
                        c.addAlien(AlienType.BROWN);
                    }, () -> {
                        throw new RuntimeException("Non ho aggiunto alieni");
                    });
            assertEquals(3, p.getShipBoard().getQuantityGuests());
        });
        // adds an empty housing and adds 2 humand in main housing
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Housing(5, Connector.TRIPLE, Connector.SINGLE, Connector.DOUBLE, Connector.SINGLE), 1, 3);
            for (int i = 0; i < 2; i++) {
                p.getShipBoard().getComponentsStream().filter(c -> c.getId() == 1).findFirst().ifPresent(c -> {
                    c.addHumanMember();
                    c.addHumanMember();
                });
            }
        });
    }

    // this method will be implemented as it is (and with the view modifying part) in the controller
    public void play_epidemic(AdventureCard card) {
        for (Player player : fly.getScoreBoard()) {
            card.startTest(fly, player, 0);
        }
    }

    @Test
    public void should_play_stardust() {
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
    public void should_play_stardust_twice() {
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
    public void play_stardust(AdventureCard card) {
        List<Player> playersReverse = new ArrayList<>(fly.getScoreBoard());
        Collections.reverse(playersReverse);
        for (Player p : playersReverse) {
            card.startTest(fly, p, 0);
            // after each modification, it will be necessary to update the view one player at once
        }
    }

    public void play_smugglers(AdventureCard card) {
        System.out.println("Smugglers");
    }

    @Test
    public void should_play_open_space() {
        AdventureCard card = new OpenSpace(1, 2);
        int setup = 2;
        if (setup == 1) {
            open_space_setup1();
        } else {
            open_space_setup2();
        }
        play_open_space(card);
        if (setup == 1) {
            /* Asserts for setup 1 with toActivate=1 for everyone
            assertEquals(9, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get())));
            assertEquals(6, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get())));
            assertEquals(4, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get())));
            assertEquals(3, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get())));
            */
            // Asserts for setup 1 with toActivate=0 for everyone
            assertEquals(7, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get())));
            assertEquals(4, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get())));
            assertEquals(2, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get())));
            assertEquals(1, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get())));
        } else {
            // Asserts for setup 2 with toActivate=0 or =-1 or =3 for everyone
            assertEquals(7, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get())));
            assertEquals(5, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get())));
            assertEquals(3, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get())));
            assertEquals(1, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get())));

            /* Asserts for setup 2 with toActivate=1 for everyone
            assertEquals(9, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get())));
            assertEquals(7, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get())));
            assertEquals(5, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get())));
            assertEquals(1, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get())));
             */
            /* Asserts for setup 2 with toActicvate=2 for everyone
            assertEquals(7, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().get())));
            assertEquals(5, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().get())));
            assertEquals(9, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().get())));
            assertEquals(1, fly.getCircuit().indexOf(Optional.of(fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().get())));
             */
        }
    }

    public void open_space_setup1() {
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Engine(5, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 4, 2);
            p.getShipBoard().addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.DOUBLE), 1, 5);
            assertEquals(1, p.getShipBoard().getBaseEnginePower());
        });
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Engine(5, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 4, 2);
            p.getShipBoard().addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.DOUBLE), 1, 5);
            assertEquals(1, p.getShipBoard().getBaseEnginePower());
        });
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Engine(5, Connector.TRIPLE, Connector.FLAT, Connector.SINGLE, Connector.SINGLE), 4, 1);
            p.getShipBoard().addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.DOUBLE), 1, 5);
            assertEquals(1, p.getShipBoard().getBaseEnginePower());
        });
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Engine(5, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 4, 2);
            p.getShipBoard().addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.DOUBLE), 1, 5);
            assertEquals(1, p.getShipBoard().getBaseEnginePower());
        });
    }

    public void open_space_setup2() {
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Stefano")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Engine(5, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 4, 2);
            p.getShipBoard().addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.DOUBLE), 1, 5);
            assertEquals(1, p.getShipBoard().getBaseEnginePower());
        });
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Antonio")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Engine(5, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 4, 2);
            p.getShipBoard().addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.DOUBLE), 1, 5);
            p.getShipBoard().addComponentToPosition(new Engine(7, Connector.TRIPLE, Connector.FLAT, Connector.SINGLE, Connector.SINGLE), 4, 1);
            assertEquals(2, p.getShipBoard().getBaseEnginePower());
        });
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Andrea")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Engine(5, Connector.TRIPLE, Connector.FLAT, Connector.SINGLE, Connector.SINGLE), 4, 1);
            assertEquals(1, p.getShipBoard().getBaseEnginePower());
        });
        fly.getScoreBoard().stream().filter(p -> p.getUsername().equals("Lorenzo")).findFirst().ifPresent(p -> {
            p.getShipBoard().addComponentToPosition(new Engine(5, Connector.TRIPLE, Connector.FLAT, Connector.DOUBLE, Connector.SINGLE), 4, 2);
            p.getShipBoard().addComponentToPosition(new Engine(7, Connector.TRIPLE, Connector.FLAT, Connector.SINGLE, Connector.SINGLE), 4, 1);
            p.getShipBoard().addComponentToPosition(new DoubleEngine(6, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.DOUBLE), 1, 5);
            p.getShipBoard().addComponentToPosition(new DoubleEngine(8, Connector.FLAT, Connector.FLAT, Connector.FLAT, Connector.SINGLE), 3, 5);
            assertEquals(2, p.getShipBoard().getBaseEnginePower());
        });
    }

    public void play_open_space(AdventureCard card) {
        for (Player p : fly.getScoreBoard()) {
            int numDoubleEngines = (int) p.getShipBoard().getComponentsStream().filter(c -> c.getType().equals(ComponentType.DOUBLE_ENGINE)).count();
            int numBatteries = p.getShipBoard().getQuantBatteries();
            int toActivate = 0;
            if (numDoubleEngines <= 0) {
                System.out.println(p.getUsername() + ": non hai doppi motori, non puoi attivare nulla");
            } else {
                System.out.println(p.getUsername() + ": hai " + numDoubleEngines + " doppi motori e " + numBatteries + " segnalini batteria, quanti motori vuoi attivare?");
                /*toActivate = 0;  //ask the player
                while(toActivate < 0 || toActivate > numDoubleEngines || toActivate > numBatteries){
                    System.out.println("Hai inserito un valore non valido (" + toActivate + ")");
                    System.out.println(p.getUsername() + ": hai " + numDoubleEngines + " doppi motori e " + numBatteries + " segnalini batteria, quanti motori vuoi attivare?");
                    toActivate = 0; //ask the player
                    p.getShipBoard().removeEnergy(toActivate);
                }*/

                // variable to modify for testing
                toActivate = -1;
                if (toActivate < 0 || toActivate > numDoubleEngines || toActivate > numBatteries) {
                    System.out.println("Hai inserito un valore non valido (" + toActivate + ")");
                    toActivate = 0;
                }
                System.out.println(toActivate);
            }
            card.startTest(fly, p, toActivate);
        }
    }

    public void play_combat_zone(AdventureCard card) {
        System.out.println("Combat zone");
    }

    public void play_meteor_swarm(AdventureCard card) {
        System.out.println("Meteor swarm");
    }

    @Test
    public void setUpAbandondShip() {
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

    public void play_abandoned_ship(AdventureCard card) {
        card.start(fly);
    }

    public void play_abandoned_station(AdventureCard card) {
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
