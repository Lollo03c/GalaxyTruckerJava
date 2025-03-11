package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.AlienType;
import org.mio.progettoingsoft.components.Depot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FlyBoardTest {

    @Test
    void should_construct_152_components(){
        FlyBoard flyBoard = new FlyBoard();

        int loadedComponents = flyBoard.getCoveredComponents().size();

        assertEquals(152, loadedComponents);
    }

    @Test
    void should_load_11_double_batteries(){
        FlyBoard fly = new FlyBoard();

        int count = 0;
        for (Component comp : fly.getCoveredComponents()){
            if (comp.getEnergyQuantity() == 2)
                count++;
        }

        assertEquals(11, count);
    }

    @Test
    void should_load_6_double_batteries(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;
        for (Component comp : fly.getCoveredComponents()){
            if (comp.getEnergyQuantity() == 3)
                count++;
        }

        assertEquals(6, count);
    }

    @Test
    void should_load_9_simple_depot(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(9, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot)component)
                .filter(depot -> !depot.getBig() && !depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_9_triple_depot(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot)component)
                .filter(depot -> depot.getBig() && !depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_17_housing(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(17, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.HOUSING))
                .count()
        );
    }

    @Test
    void should_load_8_pipe(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(8, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.PIPE))
                .count()
        );
    }

    @Test
    void should_load_6_hazard_single_depot(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot)component)
                .filter(depot -> !depot.getBig() && depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_3_hazard_double_depot(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(3, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DEPOT))
                .map(component -> (Depot)component)
                .filter(depot -> depot.getBig() && depot.getHazard())
                .count()
        );
    }

    @Test
    void should_load_21_engine(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(21, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.ENGINE))
                .count()
        );
    }

    @Test
    void should_load_9_double_engine(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(9, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DOUBLE_ENGINE))
                .count()
        );
    }

    @Test
    void should_load_25_drill(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(25, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DRILL))
                .count()
        );
    }

    @Test
    void should_load_11_double_drill(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(11, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DOUBLE_DRILL))
                .count()
        );
    }

    @Test
    void should_load_6_brown_alien_housing(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.ALIEN_HOUSING))
                .filter(component -> component.getColorAlien().equals(AlienType.BROWN))
                .count()
        );
    }

    @Test
    void should_load_6_purple_alien_housing(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(6, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.ALIEN_HOUSING))
                .filter(component -> component.getColorAlien().equals(AlienType.PURPLE))
                .count()
        );
    }

    @Test
    void should_load_8_shield(){
        FlyBoard fly = new FlyBoard();

        
        int count = 0;

        assertEquals(8, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.SHIELD))
                .count()
        );
    }

    @Test
    void should_load_AdvCards(){
        FlyBoard flyBoard = new FlyBoard();

        int loadAdvCards = flyBoard.getAdventureCards().size();

        // assertEquals(, loadAdvCards);

    }

    @Test
    void shuold_create_4_players(){
        List<String> users = new ArrayList<>(4);
        users.add("Antonio");
        users.add("Lorenzo");
        users.add("Andrea");
        users.add("Stefano");

        FlyBoard fly = new FlyBoard();

        fly.addPlayer(users.get(0));
        fly.addPlayer(users.get(0));
        fly.addPlayer(users.get(1));
        fly.addPlayer(users.get(2));
        fly.addPlayer(users.get(3));
        fly.addPlayer(users.get(3));
        fly.addPlayer("Sbagliato");

        assertEquals(4, fly.getScoryBoard().size());
        for (String user : users){
            assertTrue(fly.getScoryBoard().stream().anyMatch(
                    player -> player.getUsername().equals(user)
            ));
        }
        assertFalse(fly.getScoryBoard().stream()
                .anyMatch(player -> player.getUsername().equals("Sbagliato"))
        );
    }

    @Test
    void shold_advance_one(){
        FlyBoard board = new FlyBoard();

        Player player = new Player("test");
        board.addPlayer(player);

        board.getCircuit().set(23, Optional.of(player));
        board.moveDays(player, 2);

        assertEquals(1, board.getCircuit().indexOf(Optional.of(player)));
    }

    @Test
    void shold_advance_advance_with_other_player_between(){
        FlyBoard board = new FlyBoard();

        Player first = new Player("test");
        board.addPlayer(first);

        Player second = new Player("t");
        board.addPlayer(second);

        board.getCircuit().set(23, Optional.of(first));
        board.getCircuit().set(0, Optional.of(second));
        board.moveDays(first, 2);

        assertEquals(2, board.getCircuit().indexOf(Optional.of(first)));
        assertEquals(0, board.getCircuit().indexOf(Optional.of(second)));
    }

    @Test
    void shold_advance_retrat_with_other_player_between(){
        FlyBoard board = new FlyBoard();

        Player first = new Player("test");
        board.addPlayer(first);

        Player second = new Player("t");
        board.addPlayer(second);

        board.getCircuit().set(23, Optional.of(first));
        board.getCircuit().set(0, Optional.of(second));
        board.moveDays(second, -2);

        assertEquals(23, board.getCircuit().indexOf(Optional.of(first)));
        assertEquals(21, board.getCircuit().indexOf(Optional.of(second)));
    }


}