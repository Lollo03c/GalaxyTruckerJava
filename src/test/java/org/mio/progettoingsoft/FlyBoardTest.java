package org.mio.progettoingsoft;

import org.junit.jupiter.api.Test;
import org.mio.progettoingsoft.components.AlienType;
import org.mio.progettoingsoft.components.Depot;
import org.mio.progettoingsoft.components.EnergyDepot;

import static org.junit.jupiter.api.Assertions.*;

class FlyBoardTest {

    @Test
    void should_construct_153_components(){
        FlyBoard flyBoard = new FlyBoard();

        flyBoard.loadComponents();
        int loadedComponents = flyBoard.getCoveredComponents().size();

        assertEquals(152, loadedComponents);
    }

    @Test
    void should_load_11_double_batteries(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
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

        fly.loadComponents();
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

        fly.loadComponents();
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

        fly.loadComponents();
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

        fly.loadComponents();
        int count = 0;

        assertEquals(17, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.HOUSING))
                .count()
        );
    }

    @Test
    void should_load_8_pipe(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;

        assertEquals(8, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.PIPE))
                .count()
        );
    }

    @Test
    void should_load_6_hazard_single_depot(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
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

        fly.loadComponents();
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

        fly.loadComponents();
        int count = 0;

        assertEquals(21, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.ENGINE))
                .count()
        );
    }

    @Test
    void should_load_9_double_engine(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;

        assertEquals(9, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DOUBLE_ENGINE))
                .count()
        );
    }

    @Test
    void should_load_25_drill(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;

        assertEquals(25, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DRILL))
                .count()
        );
    }

    @Test
    void should_load_11_double_drill(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
        int count = 0;

        assertEquals(11, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.DOUBLE_DRILL))
                .count()
        );
    }

    @Test
    void should_load_6_brown_alien_housing(){
        FlyBoard fly = new FlyBoard();

        fly.loadComponents();
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

        fly.loadComponents();
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

        fly.loadComponents();
        int count = 0;

        assertEquals(8, fly.getCoveredComponents().stream()
                .filter(component -> component.getType().equals(ComponentType.SHIELD))
                .count()
        );
    }


}