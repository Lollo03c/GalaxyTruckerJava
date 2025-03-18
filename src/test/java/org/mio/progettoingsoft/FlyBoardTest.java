package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.AlienType;
import org.mio.progettoingsoft.components.Depot;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.CannotAddPlayerException;
import org.mio.progettoingsoft.exceptions.NoComponentInHandException;
import org.mio.progettoingsoft.exceptions.NoMoreComponentsException;
import org.mio.progettoingsoft.exceptions.NotEnoughSpaceForBookedComponentException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlyBoardTest {

    @Test
    void should_construct_152_components() {
        FlyBoard flyBoard = new FlyBoard();

        int loadedComponents = flyBoard.getCoveredComponents().size();

        assertEquals(152, loadedComponents);
    }

    @Test
    void should_load_11_double_batteries() {
        FlyBoard fly = new FlyBoard();

        int count = 0;
        for (Component comp : fly.getCoveredComponents()) {
            if (comp.getEnergyQuantity() == 2)
                count++;
        }

        assertEquals(11, count);
    }

    @Test
    void should_load_6_double_batteries() {
        FlyBoard fly = new FlyBoard();

        int count = 0;
        for (Component comp : fly.getCoveredComponents()) {
            if (comp.getEnergyQuantity() == 3)
                count++;
        }

        assertEquals(6, count);
    }

    @Test
    void should_load_9_simple_depot() {
        FlyBoard fly = new FlyBoard();

        int count = 0;

        assertEquals(9, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot) component)
                .filter(depot -> !depot.getBig() && !depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_9_triple_depot() {
        FlyBoard fly = new FlyBoard();

        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot) component)
                .filter(depot -> depot.getBig() && !depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_17_housing() {
        FlyBoard fly = new FlyBoard();

        int count = 0;

        assertEquals(17, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.HOUSING))
                .count()
        );
    }

    @Test
    void should_load_8_pipe() {
        FlyBoard fly = new FlyBoard();

        int count = 0;

        assertEquals(8, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.PIPE))
                .count()
        );
    }

    @Test
    void should_load_6_hazard_single_depot() {
        FlyBoard fly = new FlyBoard();

        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot) component)
                .filter(depot -> !depot.getBig() && depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_3_hazard_double_depot() {
        FlyBoard fly = new FlyBoard();

        int count = 0;

        assertEquals(3, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot) component)
                .filter(depot -> depot.getBig() && depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_21_engine() {
        FlyBoard fly = new FlyBoard();


        int count = 0;

        assertEquals(21, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.ENGINE))
                .count()
        );
    }

    @Test
    void should_load_9_double_engine() {
        FlyBoard fly = new FlyBoard();


        int count = 0;

        assertEquals(9, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DOUBLE_ENGINE))
                .count()
        );
    }

    @Test
    void should_load_25_drill() {
        FlyBoard fly = new FlyBoard();


        int count = 0;

        assertEquals(25, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DRILL))
                .count()
        );
    }

    @Test
    void should_load_11_double_drill() {
        FlyBoard fly = new FlyBoard();


        int count = 0;

        assertEquals(11, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DOUBLE_DRILL))
                .count()
        );
    }

    @Test
    void should_load_6_brown_alien_housing() {
        FlyBoard fly = new FlyBoard();


        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.ALIEN_HOUSING))
                .filter(component -> component.getColorAlien().equals(AlienType.BROWN))
                .count()
        );
    }

    @Test
    void should_load_6_purple_alien_housing() {
        FlyBoard fly = new FlyBoard();


        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.ALIEN_HOUSING))
                .filter(component -> component.getColorAlien().equals(AlienType.PURPLE))
                .count()
        );
    }

    @Test
    void should_load_8_shield() {
        FlyBoard fly = new FlyBoard();


        int count = 0;

        assertEquals(8, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.SHIELD))
                .count()
        );
    }

    @Test
    void should_load_AdvCards() {
        FlyBoard flyBoard = new FlyBoard();

        int loadAdvCards = flyBoard.getAdventureCards().size();

        assertEquals(40, loadAdvCards);

    }

    @Test
    void should_create_4_players() throws CannotAddPlayerException {
        Map<String, HousingColor> users = new HashMap<>(4);
        users.put("Antonio", HousingColor.BLUE);
        users.put("Lorenzo", HousingColor.RED);
        users.put("Andrea", HousingColor.GREEN);
        //users.put("Stefano", HousingColor.YELLOW);

        FlyBoard fly = new FlyBoard();

        for (String user : users.keySet()) {
            fly.addPlayer(user, users.get(user));
        }

        assertThrows(CannotAddPlayerException.class,
                () -> fly.addPlayer("Antonio", HousingColor.YELLOW));
        assertFalse(fly.getScoreBoard().stream()
                .anyMatch(player -> player.getColor().equals(HousingColor.YELLOW))
        );

        assertThrows(CannotAddPlayerException.class, () -> fly.addPlayer("Test", HousingColor.BLUE));
        assertFalse(fly.getScoreBoard().stream()
                .anyMatch(player -> player.getUsername().equals("Test"))
        );

        fly.addPlayer("Stefano", HousingColor.YELLOW);
        assertEquals(4, fly.getScoreBoard().size());

        assertThrows(CannotAddPlayerException.class, () -> fly.addPlayer("Test", HousingColor.BLUE));
        assertFalse(fly.getScoreBoard().stream()
                .anyMatch(player -> player.getUsername().equals("Test"))
        );

        for (String user : users.keySet()) {
            assertTrue(fly.getScoreBoard().stream().anyMatch(
                    player -> player.getUsername().equals(user)
            ));
        }

    }

    @Test
    void should_advance_one() throws CannotAddPlayerException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("test", HousingColor.BLUE);

        board.getCircuit().set(23, board.getPlayerByUsername("test"));
        board.moveDays(board.getPlayerByUsername("test").get(), 2);

        assertEquals(1, board.getCircuit().indexOf(board.getPlayerByUsername("test")));
    }

    @Test
    void should_advance_advance_with_other_player_between() throws CannotAddPlayerException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("test", HousingColor.BLUE);
        board.addPlayer("t", HousingColor.GREEN);

        board.getCircuit().set(23, board.getPlayerByUsername("test"));
        board.getCircuit().set(0, board.getPlayerByUsername("t"));
        board.moveDays(board.getPlayerByUsername("test").get(), 2);

        assertEquals(2, board.getCircuit().indexOf(board.getPlayerByUsername("test")));
        assertEquals(0, board.getCircuit().indexOf(board.getPlayerByUsername("t")));
    }

    @Test
    void should_advance_retrat_with_other_player_between() throws CannotAddPlayerException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("test", HousingColor.BLUE);
        board.addPlayer("t", HousingColor.GREEN);

        board.getCircuit().set(23, board.getPlayerByUsername("test"));
        board.getCircuit().set(0, board.getPlayerByUsername("t"));
        board.moveDays(board.getPlayerByUsername("t").get(), -2);

        assertEquals(23, board.getCircuit().indexOf(board.getPlayerByUsername("test")));
        assertEquals(21, board.getCircuit().indexOf(board.getPlayerByUsername("t")));
    }

    @Test
    void should_change_ScoreBoard() throws CannotAddPlayerException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("test", HousingColor.BLUE);
        board.addPlayer("t", HousingColor.GREEN);
        board.getCircuit().set(3, board.getPlayerByUsername("test"));
        board.getCircuit().set(1, board.getPlayerByUsername("t"));
        assertEquals(0, board.getScoreBoard().indexOf(board.getPlayerByUsername("test").get()));
        assertEquals(1, board.getScoreBoard().indexOf(board.getPlayerByUsername("t").get()));
        board.moveDays(board.getPlayerByUsername("t").get(), 3);
        assertEquals(board.getPlayerByUsername("t").get(), board.getScoreBoard().get(0));
        assertEquals(board.getPlayerByUsername("test").get(), board.getScoreBoard().get(1));
        board.moveDays(board.getPlayerByUsername("test").get(), 5);
        assertEquals(9, board.getCircuit().indexOf(board.getPlayerByUsername("test")));
        assertEquals(0, board.getScoreBoard().indexOf(board.getPlayerByUsername("test").get()));
    }

    @Test
    public void should_draw_a_component() throws NoMoreComponentsException, CannotAddPlayerException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("Stefano", HousingColor.BLUE);
        Component c = board.drawComponent();
        assertNotNull(c);
        board.getPlayerByUsername("Stefano").get().setInHand(c);
        assertEquals(c, board.getPlayerByUsername("Stefano").get().getInHand());
    }

    @Test
    public void should_draw_and_refuse_a_component() throws NoMoreComponentsException, CannotAddPlayerException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("Stefano", HousingColor.BLUE);
        //input utente: pesca coperto
        Component c = board.drawComponent();
        assertNotNull(c);

        board.getPlayerByUsername("Stefano").get().setInHand(c);

        assertEquals(c, board.getPlayerByUsername("Stefano").get().getInHand());

        // input utente: rifiuta componente
        Component refused = board.getPlayerByUsername("Stefano").get().refuseComponentInHand();

        assertNotNull(refused);
        assertEquals(c, refused);

        board.addUncoveredComponent(refused);

        assertEquals(1, board.getUncoveredComponents().size());
        assertThrows(NoComponentInHandException.class, () -> board.getPlayerByUsername("Stefano").get().refuseComponentInHand());
    }

    @Test
    public void should_draw_and_book_component() throws CannotAddPlayerException, NoMoreComponentsException, NotEnoughSpaceForBookedComponentException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("Stefano", HousingColor.BLUE);
        //input utente: pesca coperto
        Component c = board.drawComponent();
        assertNotNull(c);

        board.getPlayerByUsername("Stefano").get().setInHand(c);

        assertEquals(c, board.getPlayerByUsername("Stefano").get().getInHand());

        //input utente: prenota componente
        board.getPlayerByUsername("Stefano").get().bookComponentInHand();

        assertNull(board.getPlayerByUsername("Stefano").get().getInHand());
        assertEquals(c, board.getPlayerByUsername("Stefano").get().getShipBoard().getBookedComponents()[0]);
    }

    @Test
    public void should_draw_and_book_three_component_and_fail() throws NoMoreComponentsException, CannotAddPlayerException, NotEnoughSpaceForBookedComponentException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("Stefano", HousingColor.BLUE);

        //input utente: pesca coperto
        Component c = board.drawComponent();
        assertNotNull(c);

        board.getPlayerByUsername("Stefano").get().setInHand(c);

        assertEquals(c, board.getPlayerByUsername("Stefano").get().getInHand());

        //input utente: prenota componente
        board.getPlayerByUsername("Stefano").get().bookComponentInHand();

        assertNull(board.getPlayerByUsername("Stefano").get().getInHand());
        assertEquals(c, board.getPlayerByUsername("Stefano").get().getShipBoard().getBookedComponents()[0]);

        //input utente: pesca coperto
        c = board.drawComponent();
        assertNotNull(c);

        board.getPlayerByUsername("Stefano").get().setInHand(c);

        assertEquals(c, board.getPlayerByUsername("Stefano").get().getInHand());

        //input utente: prenota componente
        board.getPlayerByUsername("Stefano").get().bookComponentInHand();

        assertNull(board.getPlayerByUsername("Stefano").get().getInHand());
        assertEquals(c, board.getPlayerByUsername("Stefano").get().getShipBoard().getBookedComponents()[1]);

        //input utente: pesca coperto
        c = board.drawComponent();
        assertNotNull(c);

        board.getPlayerByUsername("Stefano").get().setInHand(c);

        assertEquals(c, board.getPlayerByUsername("Stefano").get().getInHand());

        //input utente: prenota componente
        assertThrows(NotEnoughSpaceForBookedComponentException.class, () -> board.getPlayerByUsername("Stefano").get().bookComponentInHand());
    }

    @Test
    public void should_choose_from_uncovered_components() throws CannotAddPlayerException, NoMoreComponentsException {
        FlyBoard board = new FlyBoard();
        board.addPlayer("Stefano", HousingColor.BLUE);

        //input utente: pesca coperto
        Component c = board.drawComponent();
        assertNotNull(c);

        board.getPlayerByUsername("Stefano").get().setInHand(c);

        assertEquals(c, board.getPlayerByUsername("Stefano").get().getInHand());

        //input utente: rifiuta componente
        Component refusedComponent = board.getPlayerByUsername("Stefano").get().refuseComponentInHand();
        board.addUncoveredComponent(refusedComponent);

        assertNull(board.getPlayerByUsername("Stefano").get().getInHand());
        assertTrue(board.getUncoveredComponents().contains(refusedComponent));

        //input utente: sceglie componente scartato
        c = board.chooseComponentFromUncovered(0);
        board.getPlayerByUsername("Stefano").get().setInHand(c);

        assertNotNull(board.getPlayerByUsername("Stefano").get().getInHand());
        assertEquals(c, board.getPlayerByUsername("Stefano").get().getInHand());
        assertFalse(board.getUncoveredComponents().contains(refusedComponent));

        //input utente: rifiuta componente
        refusedComponent = board.getPlayerByUsername("Stefano").get().refuseComponentInHand();
        board.addUncoveredComponent(refusedComponent);

        assertNull(board.getPlayerByUsername("Stefano").get().getInHand());
        assertTrue(board.getUncoveredComponents().contains(refusedComponent));
    }
}